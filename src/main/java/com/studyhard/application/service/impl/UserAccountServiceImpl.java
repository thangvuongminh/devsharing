package com.studyhard.application.service.impl;

import com.studyhard.application.dto.request.ChangePasswordRequest;
import com.studyhard.application.dto.request.ResetPasswordRequest;
import com.studyhard.application.dto.request.UserLoginRequest;
import com.studyhard.application.dto.request.UserRegisterRequest;
import com.studyhard.application.dto.response.UserLoginResponse;
import com.studyhard.application.dto.response.UserRegistrationResponse;
import com.studyhard.application.entity.Role;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.UserRole;
import com.studyhard.application.entity.UserVerification;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.mapper.UserMapper;
import com.studyhard.application.model.RoleEnum;
import com.studyhard.application.model.UserStatus;
import com.studyhard.application.model.UserVerificationChannel;
import com.studyhard.application.repository.RoleRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.UserRoleRepository;
import com.studyhard.application.repository.UserVerificationRepository;
import com.studyhard.application.service.NotificationService;
import com.studyhard.application.service.UserAccountService;
import com.studyhard.application.utils.GenerateToken;
import com.studyhard.application.utils.RandomOtp;
import com.studyhard.application.utils.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class UserAccountServiceImpl implements UserAccountService {
  NotificationService notificationService;
  RoleRepository roleRepository;
  PasswordEncoder passwordEncoder;
  UserRepository userRepository;
  UserRoleRepository userRoleRepository;
  UserVerificationRepository userVerificationRepository;
  UserMapper userMapper;
  AuthenticationManager authenticationManager;
  GenerateToken generateToken;
  RedisTemplate<String, Object> redisTemplate;
  JwtDecoder jwtDecoder;
  RandomOtp randomOtp;
  @Override
  @Transactional
  public UserRegistrationResponse registerUser(UserRegisterRequest request) {
    Role role = getRoleConsumer();
    checkUserExists(request.getUsername());
    checkEmailExists(request.getEmail());
    User user = saveUserFromUserRegisterDto(request);
    saveUserRoleFromUserRegisterDto(role, user);
    sendEmailVerification(user);
    UserRegistrationResponse responseDto = userMapper.toUserRegistrationResponse(user);
    return responseDto;
  }

  @Override
  public UserLoginResponse loginUser(UserLoginRequest userLoginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(),
            userLoginRequest.getPassword()));
    String username = userLoginRequest.getUsername();
    User user = userRepository.findByUserName(username)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND));
    List<UserRole> userRole = userRoleRepository.findByUser(user);
    String[] roles = userRole.stream().map(userRole1 ->
        userRole1.getRole().getRoleName().name()
    ).toArray(String[]::new);
    String accessToken = generateToken.createToken(user.getId(), TokenType.ACCESS_TOKEN, roles);
    String refreshToken = generateToken.createToken(user.getId(), TokenType.REFRESH_TOKEN, roles);
    return UserLoginResponse.builder()
        .userId(user.getId())
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  public void logoutUser(HttpServletResponse response) {
    JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder
        .getContext().getAuthentication();
    Map<String, Object> claims = jwtAuthenticationToken.getToken().getClaims();
    String jti = (String) claims.get("jti");
    Instant exp = (Instant) claims.get("exp");
    Long timeToLive = exp.getEpochSecond() - Instant.now().getEpochSecond();
    redisTemplate.opsForValue().set("blacklist " + jti, "logout", Duration.ofSeconds(timeToLive));
    ResponseCookie responseCookie = ResponseCookie.from("studyHard", "")
        .secure(false)
        .path("/")
        .httpOnly(true)
        .maxAge(0)
        .build();
    response.addHeader("Set-Cookie", responseCookie.toString());

  }

  @Override
  public UserLoginResponse refreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    String refreshToken= Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("studyHard")).map(
         Cookie::getValue) .findFirst().orElseThrow(() -> new StudyHardException(ExceptionEnum.REFRESH_TOKEN_MISSING));
    Jwt jwt=null;
     try {
       jwt= jwtDecoder.decode(refreshToken);
     }catch (JwtException e) {
       e.printStackTrace();
       throw  new StudyHardException(ExceptionEnum.INVALID_TOKEN);
     }
     Map<String, Object> claims = jwt.getClaims();
     Instant exp = (Instant) claims.get("exp");
     Instant now = Instant.now();
     if (now.isAfter(exp)) {
       throw new StudyHardException(ExceptionEnum.INVALID_TOKEN);
     }
     Long userId= (Long) claims.get("userId");
     List<UserRole> userRoles=userRoleRepository.findByUserId(userId);
     String[] roles =userRoles.stream().map(ur -> ur.getRole().getRoleName().name()).distinct().toArray(String[]::new);
     String accessToken = generateToken.createToken(userId, TokenType.ACCESS_TOKEN, roles);
     return UserLoginResponse.builder()
         .accessToken(accessToken)
         .userId(userId)
         .build();
  }

  @Override
  public void forgotPassword(String email) {
    User user = userRepository.findByEmail(email);
    if(user==null){
      throw new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND);
    }
    String otpCode=String.valueOf(randomOtp.generateOtp());
    String subject = "StudyHard Yêu cầu khôi phục mật khẩu";
    String msgBody = """
    Chào bạn,
    
    Bạn vừa gửi yêu cầu khôi phục mật khẩu. Vui lòng sử dụng mã xác minh dưới đây:
    
    Mã OTP của bạn là:  %s
    
    (Mã này có hiệu lực trong 5 phút . Tuyệt đối không chia sẻ mã này cho bất kỳ ai).
    
    Nếu không phải bạn thực hiện yêu cầu này, hãy đổi mật khẩu ngay lập tức hoặc liên hệ hỗ trợ.
    """.formatted(otpCode);
    notificationService.sendEmail(subject,msgBody,email);
    redisTemplate.opsForValue().set(email,otpCode, Duration.ofSeconds(300));
  }

  @Override
  public void resetPassword(ResetPasswordRequest request) {
    User user = userRepository.findByEmail(request.getEmail());
    if(user==null){
      throw new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND);
    }
    String otp =(String) redisTemplate.opsForValue().get(request.getEmail());
    if (otp==null){
      throw new StudyHardException(ExceptionEnum.INVALID_OTP);
    }
    if (otp.equals(request.getOtp())){
      user.setPassword(passwordEncoder.encode(request.getPassword()));
      redisTemplate.delete(request.getEmail());
      userRepository.save(user);
    }else  {
      throw new StudyHardException(ExceptionEnum.INVALID_OTP);
    }
  }

  @Override
  public void changePassword(ChangePasswordRequest changePasswordRequest) {
     JwtAuthenticationToken authentication=(JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
     Long userId=(Long) authentication.getToken().getClaims().get("userId");
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND));
    if(passwordEncoder.matches(changePasswordRequest.getPassword(),user.getPassword())){
      user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
    }else {
      throw new StudyHardException(ExceptionEnum.PASSWORD_NOT_MATCH);
    }
  }

  public void sendEmailVerification(User user) {
    try {
      UserVerification userVerification = new UserVerification();
      userVerification.setUser(user);
      userVerification.setVerificationCode(UUID.randomUUID().toString());
      userVerification.setReceiver(user.getEmail());
      Instant expireEmailVerificationTime = Instant.now().plus(1, ChronoUnit.DAYS);
      userVerification.setExpiredAt(expireEmailVerificationTime);
      userVerification.setChannel(UserVerificationChannel.EMAIL.name());
      userVerification.setCreateBy(user.getId());
      userVerification.setUpdateBy(user.getId());
      userVerification.setCreateAt(Instant.now());
      userVerification.setUpdateAt(Instant.now());
      userVerificationRepository.save(userVerification);
    } catch (Exception e) {
      e.printStackTrace();
      log.info("Error in sending email [{}] verification with user [{}]", user.getEmail(),
          user.getId());
    }
  }

  public Role getRoleConsumer() {
    return roleRepository.findByRoleName(RoleEnum.CONSUMER)
        .orElseThrow(() -> new RuntimeException("Role not found"));
  }

  public void checkUserExists(String username) {
    var user = userRepository.findByUserName(username);
    if (user.isPresent()) {
      throw new StudyHardException(ExceptionEnum.USERNAME_ALREADY_EXISTS);
    }
  }
  public  void checkEmailExists(String email) {
    var user = userRepository.findByEmail(email);
    if (user!=null){
      throw new StudyHardException(ExceptionEnum.EMAIL_ALREADY_EXISTS);
    }
  }

  public User saveUserFromUserRegisterDto(UserRegisterRequest request) {
    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .userName(request.getUsername())
        .createAt(Instant.now())
        .updateAt(Instant.now())
        .status(UserStatus.PENDING_ACTIVE)
        .createBy(0)
        .updateBy(0)
        .build();
    userRepository.save(user);
    return user;
  }

  public void saveUserRoleFromUserRegisterDto(Role role, User user) {
    UserRole userRole = UserRole.builder()
        .user(user)
        .role(role)
        .createAt(Instant.now())
        .updateAt(Instant.now())
        .build();
    userRoleRepository.save(userRole);
  }
}
