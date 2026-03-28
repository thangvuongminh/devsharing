package com.studyhard.application.service;

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
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.mapper.UserMapper;
import com.studyhard.application.model.RoleEnum;
import com.studyhard.application.model.UserVerificationChannel;
import com.studyhard.application.repository.RoleRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.UserRoleRepository;
import com.studyhard.application.repository.UserVerificationRepository;
import com.studyhard.application.service.impl.UserAccountServiceImpl;
import com.studyhard.application.utils.GenerateToken;
import com.studyhard.application.utils.RandomOtp;
import com.studyhard.application.utils.TokenType;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.InstanceOfAssertFactories.DURATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  UserRepository userRepository;
  @Mock
  RoleRepository roleRepository;
  @InjectMocks
  UserAccountServiceImpl userAccountService;
  UserRole userRole;
  @Mock
  PasswordEncoder passwordEncoder;
  @Mock
  UserMapper userMapper;
  @Mock
  UserRoleRepository userRoleRepository;
  @Mock
  UserVerificationRepository userVerificationRepository;
  @Mock
  RedisTemplate<String, Object> redisTemplate;

  @Mock
  ValueOperations<String, Object> valueOperations;

  @Mock
  RandomOtp randomOtp;
  Role role;
  User user;
  UserVerification userVerification;
  @Mock
  NotificationService notificationService;
  @Mock
  AuthenticationManager authenticationManager;
  @Mock
  GenerateToken generateToken;
  @Mock
  JwtAuthenticationToken jwtAuthenticationToken;
  @Mock
  HttpServletResponse httpServletResponse;

  @BeforeEach
  void setUp() {
    lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    role = Role.builder()
        .roleName(RoleEnum.CONSUMER)
        .description("Người mua nội dung, học viên")
        .build();
    user = User.builder()
        .username("thangVm")
        .password("123456").email("chaudiensdk5@gmail.com")
            .
        build();
    userRole = UserRole.builder()
        .user(user)
        .role(role)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
    userVerification = UserVerification.builder()
        .user(user)
        .channel(UserVerificationChannel.EMAIL.name())
        .createdAt(Instant.now())
        .expiredAt(Instant.now().plus(1, ChronoUnit.DAYS))
        .receiver(user.getEmail())
        .verificationCode(UUID.randomUUID().toString())
        .build();
  }

  @Mock
  Authentication authentication;

  @Test
  public void registerUserTest() {
    given(roleRepository.findByRoleName(RoleEnum.CONSUMER)).willReturn(Optional.of(role));
    given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.empty());
    given(userRepository.findByEmail(user.getEmail())).willReturn(null);
    given(userRoleRepository.save(any(UserRole.class))).willReturn(null);
    given(userVerificationRepository.save(any(UserVerification.class))).willReturn(
        userVerification);
    given(passwordEncoder.encode(user.getPassword())).willReturn("123456" + user.getPassword());
    given(randomOtp.generateOtp()).willReturn(294294);
    UserRegistrationResponse userRegistrationResponse = UserRegistrationResponse.builder()
        .email(user.getEmail())
        .username(user.getUsername())
        .build();
    UserRegisterRequest request = UserRegisterRequest.builder()
        .email(user.getEmail())
        .password(user.getPassword())
        .username(user.getUsername())
        .build();
    given(userMapper.toUserRegistrationResponse(any(User.class))).willReturn(
        userRegistrationResponse);
    UserRegistrationResponse userRegistrationResponse1 = userAccountService.registerUser(request);

    assertThat(userRegistrationResponse1).isNotNull();
    assertThat(userRegistrationResponse1.getEmail()).isEqualTo(user.getEmail());
    assertThat(userRegistrationResponse1.getUsername()).isEqualTo(user.getUsername());

    verify(userMapper).toUserRegistrationResponse(any(User.class));
    verify(notificationService).sendEmail(anyString(), anyString(), eq(user.getEmail()));
    verify(valueOperations).set(eq(user.getEmail()), eq(294294), any(Duration.class));
    verify(userRepository).save(any(User.class));
    verify(userRoleRepository).save(any(UserRole.class));
    verify(userVerificationRepository).save(any(UserVerification.class));
  }

  @Test
  public void loginUserTest() {
    UserLoginRequest userLoginRequest = UserLoginRequest.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .build();
    given(authenticationManager.authenticate(
        any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
    given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
    given(userRoleRepository.findByUser(user)).willReturn(List.of(userRole));
    given(generateToken.createToken(user.getId(), TokenType.ACCESS_TOKEN,
        userRole.getRole().getRoleName().name())).willReturn("accessToken");
    given(generateToken.createToken(user.getId(), TokenType.REFRESH_TOKEN,
        userRole.getRole().getRoleName().name())).willReturn("refreshToken");
    UserLoginResponse userLoginResponse = userAccountService.loginUser(userLoginRequest);
    assertThat(userLoginResponse).isNotNull();
    assertThat(userLoginResponse.getUserId()).isEqualTo(user.getId());
    assertThat(userLoginResponse.getAccessToken()).isEqualTo("accessToken");
    assertThat(userLoginResponse.getRefreshToken()).isEqualTo("refreshToken");
  }
  @Test
  public void loginUserRawCredentialsTest() {
    UserLoginRequest userLoginRequest = UserLoginRequest.builder()
        .username(user.getUsername() + "e")
        .password(user.getPassword())
        .build();
    given(authenticationManager.authenticate(
        any(UsernamePasswordAuthenticationToken.class))).willThrow(BadCredentialsException.class);
    Assertions.assertThrows(BadCredentialsException.class, () -> {
      userAccountService.loginUser(userLoginRequest);
    });
  }

  @Test
  public void logoutUserTest() {
    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("jti", "some-jti-value")
        .claim("id", "4")
        .claim("exp",Instant.now().plus(30,ChronoUnit.MINUTES))
        .build();
    JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
    SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
    userAccountService.logoutUser(httpServletResponse);
    verify(valueOperations).set(anyString(), eq("logout"), any(Duration.class));
    verify(httpServletResponse).addHeader(eq("Set-Cookie"), contains("studyHard="));
  }
  @Test
  public void changPasswordTest() {
    ChangePasswordRequest changePasswordRequest= ChangePasswordRequest.builder()
        .password(user.getPassword())
        .newPassword("12345678910")
        .build();
    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("jti", "some-jti-value")
        .claim("userId", 4)
        .claim("exp",Instant.now().plus(30,ChronoUnit.MINUTES))
        .build();
    JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
    SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
    given(userRepository.findById(any())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(changePasswordRequest.getPassword(),user.getPassword())).willReturn(true);
    given(passwordEncoder.encode(changePasswordRequest.getNewPassword())).willReturn("12345678910");
    given(userRepository.save(user)).willReturn(user);
    userAccountService.changePassword(changePasswordRequest);
    Assertions.assertEquals( changePasswordRequest.getNewPassword(),user.getPassword());
    verify(userRepository).save(user);

  }
  @Test
  public void changPasswordThrowTest() {
    ChangePasswordRequest changePasswordRequest= ChangePasswordRequest.builder()
        .password(user.getPassword() + "e")
        .newPassword("12345678910")
        .build();
    Jwt jwt = Jwt.withTokenValue("token")
        .header("alg", "none")
        .claim("jti", "some-jti-value")
        .claim("userId", 4)
        .claim("exp",Instant.now().plus(30,ChronoUnit.MINUTES))
        .build();
    JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
    SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
    given(userRepository.findById(any())).willReturn(Optional.of(user));
    given(passwordEncoder.matches(changePasswordRequest.getPassword(),user.getPassword())).willReturn(false);
    Assertions.assertThrows(StudyHardException.class, () -> {
      userAccountService.changePassword(changePasswordRequest);
    });
  }
  @Test
  public  void resetPasswordTest(){
    given(userRepository.findByEmail(user.getEmail())).willReturn(user);
    ResetPasswordRequest request= ResetPasswordRequest.builder()
        .email(user.getEmail())
        .password("0987654321")
        .otp("123")
        .build();
    given(redisTemplate.opsForValue()).willReturn(valueOperations);
    given(valueOperations.get(request.getEmail())).willReturn("123");
    given(passwordEncoder.encode(request.getPassword())).willReturn("encoded_password");
    given(userRepository.save(user)).willReturn(user);
    userAccountService.resetPassword(request);
    Assertions.assertEquals("encoded_password",user.getPassword());
    verify(userRepository).save(user);
    verify(redisTemplate).delete(request.getEmail());
  }
  @Test
  public  void resetPasswordTestThrow(){
    given(userRepository.findByEmail(user.getEmail())).willReturn(user);
    ResetPasswordRequest request= ResetPasswordRequest.builder()
        .email(user.getEmail())
        .password("0987654321")
        .otp("123")
        .build();
    given(redisTemplate.opsForValue()).willReturn(valueOperations);
    given(valueOperations.get(request.getEmail())).willReturn("456");
    Assertions.assertThrows(StudyHardException.class, () -> {
      userAccountService.resetPassword(request);
    });
    verify(userRepository,never()).save(user);
  }
}