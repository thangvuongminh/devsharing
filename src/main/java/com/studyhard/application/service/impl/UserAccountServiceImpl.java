package com.studyhard.application.service.impl;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import com.studyhard.application.config.properties.GoogleApiProperties;
import com.studyhard.application.dto.request.BecomeCreatorRequest;
import com.studyhard.application.dto.request.ChangePasswordRequest;
import com.studyhard.application.dto.request.ResetPasswordRequest;
import com.studyhard.application.dto.request.UserLoginRequest;
import com.studyhard.application.dto.request.UserRegisterRequest;
import com.studyhard.application.dto.response.UserLoginResponse;
import com.studyhard.application.dto.response.UserRegistrationResponse;
import com.studyhard.application.dto.response.GoogleUserResponse;
import com.studyhard.application.entity.BecomeCreator;
import com.studyhard.application.entity.BecomeCreatorMessagesImages;
import com.studyhard.application.entity.Role;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.UserRole;
import com.studyhard.application.entity.UserVerification;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.mapper.UserMapper;
import com.studyhard.application.model.BecomeCreatorStatus;
import com.studyhard.application.model.RoleEnum;
import com.studyhard.application.model.TypeFile;
import com.studyhard.application.model.UserStatus;
import com.studyhard.application.model.UserVerificationChannel;
import com.studyhard.application.repository.BecomeCreatorMessagesImagesRepository;
import com.studyhard.application.repository.BecomeCreatorRepository;
import com.studyhard.application.repository.RoleRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.UserRoleRepository;
import com.studyhard.application.repository.UserVerificationRepository;
import com.studyhard.application.service.NotificationService;
import com.studyhard.application.service.UserAccountService;
import com.studyhard.application.utils.GenerateToken;
import com.studyhard.application.utils.RandomOtp;
import com.studyhard.application.utils.TokenType;
import com.studyhard.application.utils.UserExtractor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class UserAccountServiceImpl implements UserAccountService {
  BecomeCreatorRepository becomeCreatorRepository;
  BecomeCreatorMessagesImagesRepository becomeCreatorMessagesImagesRepository;
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
  GoogleApiProperties googleApiProperties;
  ChatClient chatClient;
  FileStorageServiceImpl fileStorageService;
  @Override
  @Transactional
  public UserRegistrationResponse registerUser(UserRegisterRequest request) {
    Role role = getRoleConsumer();
    checkUserExists(request.getUsername());
    checkEmailExists(request.getEmail());
    User user = saveUserFromUserRegisterDto(request);
    saveUserRoleFromUserRegisterDto(role, user);
    sendEmailVerification(user);
    return userMapper.toUserRegistrationResponse(user);
  }

  @Override
  public UserLoginResponse loginUser(UserLoginRequest userLoginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(),
            userLoginRequest.getPassword()));
    String username = userLoginRequest.getUsername();
    User user = userRepository.findByUsername(username)
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
    String refreshToken = Arrays.stream(cookies)
        .filter(cookie -> cookie.getName().equals("studyHard")).map(
            Cookie::getValue).findFirst()
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.REFRESH_TOKEN_MISSING));
    Jwt jwt = null;
    try {
      jwt = jwtDecoder.decode(refreshToken);
    } catch (JwtException e) {
      e.printStackTrace();
      throw new StudyHardException(ExceptionEnum.INVALID_TOKEN);
    }
    Map<String, Object> claims = jwt.getClaims();
    Instant exp = (Instant) claims.get("exp");
    Instant now = Instant.now();
    if (now.isAfter(exp)) {
      throw new StudyHardException(ExceptionEnum.INVALID_TOKEN);
    }
    Long userId = (Long) claims.get("userId");
    List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
    String[] roles = userRoles.stream().map(ur -> ur.getRole().getRoleName().name()).distinct()
        .toArray(String[]::new);
    String accessToken = generateToken.createToken(userId, TokenType.ACCESS_TOKEN, roles);
    return UserLoginResponse.builder()
        .accessToken(accessToken)
        .userId(userId)
        .build();
  }

  @Override
  public void forgotPassword(String email) {
    User user = userRepository.findByEmail(email);
    if (user == null) {
      throw new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND);
    }
    String otpCode = String.valueOf(randomOtp.generateOtp());
    String subject = "StudyHard Yêu cầu khôi phục mật khẩu";
    String msgBody = """
        Chào bạn,
        
        Bạn vừa gửi yêu cầu khôi phục mật khẩu. Vui lòng sử dụng mã xác minh dưới đây:
        
        Mã OTP của bạn là:  %s
        
        (Mã này có hiệu lực trong 5 phút . Tuyệt đối không chia sẻ mã này cho bất kỳ ai).
        
        Nếu không phải bạn thực hiện yêu cầu này, hãy đổi mật khẩu ngay lập tức hoặc liên hệ hỗ trợ.
        """.formatted(otpCode);
    notificationService.sendEmail(subject, msgBody, email);
    redisTemplate.opsForValue().set(email, otpCode, Duration.ofSeconds(300));
  }

  @Override
  public void resetPassword(ResetPasswordRequest request) {
    User user = userRepository.findByEmail(request.getEmail());
    if (user == null) {
      throw new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND);
    }
    String otp = (String) redisTemplate.opsForValue().get(request.getEmail());
    if (otp == null) {
      throw new StudyHardException(ExceptionEnum.INVALID_OTP);
    }
    if (otp.equals(request.getOtp())) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
      redisTemplate.delete(request.getEmail());
      userRepository.save(user);
    } else {
      throw new StudyHardException(ExceptionEnum.INVALID_OTP);
    }
  }

  @Override
  public void changePassword(ChangePasswordRequest changePasswordRequest) {
    User user = userRepository.findById(UserExtractor.getUserId())
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND));
    if (passwordEncoder.matches(changePasswordRequest.getPassword(), user.getPassword())) {

      user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
      userRepository.save(user);

    } else {
      throw new StudyHardException(ExceptionEnum.PASSWORD_NOT_MATCH);
    }
  }

  @Override
  @Transactional
  public UserLoginResponse loginByGoogle(String code) {
    // Get accesstoken
    Map<String,String> headers = new HashMap<>();
    headers.put("client_id",googleApiProperties.getClientId());
    headers.put("client_secret",googleApiProperties.getClientSecret());
    headers.put("grant_type",googleApiProperties.getGrantType());
    headers.put("redirect_uri",googleApiProperties.getRedirectUri());
    headers.put("code",code);
    RestClient restClient= RestClient.builder()
        .defaultHeaders(httpHeaders -> httpHeaders.setAll(headers))
        .baseUrl(googleApiProperties.getToken())
        .build();
    record  GoogleAccessResponse(String accessToken) {}
    GoogleAccessResponse googleAccessResponse=restClient.post().retrieve().body(GoogleAccessResponse.class);
    int i=0;
//
//
//
//
//
//
//
//
//    ///
//    RestClient restClient = RestClient.builder()
//        .baseUrl("https://www.googleapis.com/oauth2/v1/userinfo")
//        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + code)
//        .build();
//    GoogleUserResponse googleUserResponse = restClient.get().retrieve()
//        .body(GoogleUserResponse.class);
//    User user = userRepository.findByEmail(googleUserResponse.getEmail());
//    Role role = getRoleConsumer();
//    if (user == null) {
//
//      user = User.builder()
//          .status(UserStatus.ACTIVE)
//          .username(googleUserResponse.getEmail())
//          .password(passwordEncoder.encode(UUID.randomUUID().toString()))
//          .email(googleUserResponse.getEmail())
//          .fullName(googleUserResponse.getGiven_name() + googleUserResponse.getFamily_name())
//          .createdAt(Instant.now())
//          .updatedAt(Instant.now())
//          .build();
//      userRepository.save(user);
//      UserRole userRole = UserRole.builder()
//          .user(user)
//          .role(role)
//          .createdAt(Instant.now())
//          .updatedAt(Instant.now())
//          .build();
//      userRoleRepository.save(userRole);
//    }
//    List<UserRole> userRole = userRoleRepository.findByUser(user);
//    String[] roles = userRole.stream().map(userRole1 ->
//        userRole1.getRole().getRoleName().name()
//    ).toArray(String[]::new);
//    String accessToken = generateToken.createToken(user.getId(), TokenType.ACCESS_TOKEN, roles);
//    String refreshToken = generateToken.createToken(user.getId(), TokenType.REFRESH_TOKEN, roles);
//
//    return UserLoginResponse.builder()
//        .accessToken(accessToken)
//        .userId(user.getId())
//        .build();
    return  null;
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
      userVerification.setCreatedAt(Instant.now());
      userVerificationRepository.save(userVerification);
      Integer otp = randomOtp.generateOtp();
      notificationService.sendEmail("Email verification study-hard", """
          Hi there,
          
          Thank you for joining study-hard! 
          
          To verify your account and get started, please use the following one-time password (OTP):
          
          %d
          
          Note: This code is valid for only 5 minutes. For security reasons, please do not share this code with anyone.
          
          If you did not request this email, you can safely ignore it.
          
          Study hard and stay focused,
          The study-hard Team
          """.formatted(otp), user.getEmail());
      redisTemplate.opsForValue().set(user.getEmail(), otp, Duration.ofSeconds(300));
    } catch (Exception e) {
      e.printStackTrace();
      log.info("Error in sending email [{}] verification with user [{}]", user.getEmail(),
          user.getId());
    }
  }

  public Role getRoleConsumer() {
    return roleRepository.findByRoleName(RoleEnum.CONSUMER)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.ROLE_NOT_FOUND));
  }

  public void checkUserExists(String username) {
    var user = userRepository.findByUsername(username);
    if (user.isPresent()) {
      throw new StudyHardException(ExceptionEnum.USERNAME_ALREADY_EXISTS);
    }
  }

  public void checkEmailExists(String email) {
    var user = userRepository.findByEmail(email);
    if (user != null) {
      throw new StudyHardException(ExceptionEnum.EMAIL_ALREADY_EXISTS);
    }
  }

  public User saveUserFromUserRegisterDto(UserRegisterRequest request) {
    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .username(request.getUsername())
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .status(UserStatus.PENDING_ACTIVE)
        .build();
    userRepository.save(user);
    return user;
  }

  public void saveUserRoleFromUserRegisterDto(Role role, User user) {
    UserRole userRole = UserRole.builder()
        .user(user)
        .role(role)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
    userRoleRepository.save(userRole);
  }

  public void saveBecomeCreator(BecomeCreator becomeCreator, String message,List<String> url){
      BecomeCreatorMessagesImages becomeCreatorMessagesImages=null;
      List<BecomeCreatorMessagesImages> becomeCreatorMessagesImagesList=new ArrayList<>();
      for(String urlDetail : url){
        becomeCreatorMessagesImages=BecomeCreatorMessagesImages.builder()
            .message(message)
            .becomeCreator(becomeCreator)
            .thumbUrl(urlDetail)
            .build();
        becomeCreatorMessagesImagesList.add(becomeCreatorMessagesImages);
      }
      becomeCreatorMessagesImagesRepository.save(becomeCreatorMessagesImages);
  }

  @Override
  @Transactional
  public String becomeCreator(BecomeCreatorRequest becomeCreatorRequest)  {
     Long userId=UserExtractor.getUserId();
     User user=userRepository.findById(userId).orElseThrow(()
         -> new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND)
     );
    BecomeCreator becomeCreator=BecomeCreator.builder()
        .createAt(Instant.now())
        .status(BecomeCreatorStatus.PENDING)
        .user(user)
        .build();
    becomeCreatorRepository.save(becomeCreator);
     List<BecomeCreatorRequest.BecomeCreatorDesc>  becomeCreatorDescs = becomeCreatorRequest.getBecomeCreatorDescs();
     List<UserMessage> userMessages= new ArrayList<>();
     for(BecomeCreatorRequest.BecomeCreatorDesc becomeCreatorDesc:becomeCreatorDescs) {
       String message=becomeCreatorDesc.getMessage();
       List<MultipartFile> files=becomeCreatorDesc.getEvidences();
       List<Media> mediaList=new ArrayList<>();
       for(MultipartFile multipartFile:files) {
         mediaList.add(new Media(MimeTypeUtils.IMAGE_JPEG, multipartFile.getResource()));
       }
       List<String> listNameFile= fileStorageService.saveFile(files, TypeFile.BECOME_CREATOR);
       saveBecomeCreator(becomeCreator,message,listNameFile);
       UserMessage userMessage1=UserMessage.builder()
           .media(mediaList)
           .text(message)
           .build();
       userMessages.add(userMessage1);
     }

    return  chatClient.prompt().advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID,userId))
        .messages(userMessages.toArray(new UserMessage[0])).call().content();
  }
}
