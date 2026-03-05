package com.studyhard.application.service.impl;

import com.studyhard.application.dto.request.UserLoginRequest;
import com.studyhard.application.dto.request.UserRegisterRequest;
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
import com.studyhard.application.service.UserAccountService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Log4j2
public class UserAccountServiceImpl implements UserAccountService {
  RoleRepository roleRepository;
  PasswordEncoder passwordEncoder;
  UserRepository userRepository;
  UserRoleRepository userRoleRepository;
  UserVerificationRepository userVerificationRepository;
  UserMapper userMapper;
  AuthenticationManager authenticationManager;
  @Override
  @Transactional
  public UserRegistrationResponse registerUser(UserRegisterRequest request) {
    Role role = getRoleConsumer();
    checkUserExists(request.getUsername());
    User user = saveUserFromUserRegisterDto(request);
    saveUserRoleFromUserRegisterDto(role, user);
    sendEmailVerification(user);
    UserRegistrationResponse responseDto = userMapper.toUserRegistrationResponse(user);
    return responseDto;
  }

  @Override
  public void loginUser(UserLoginRequest userLoginRequest) {
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword()));
    log.info("Login success");
  }

  public void  sendEmailVerification(User user){
    try{
      UserVerification userVerification=new UserVerification();
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
    }catch (Exception e){
      e.printStackTrace();
      log.info("Error in sending email [{}] verification with user [{}]",user.getEmail(),user.getId());
    }
  }
  public Role getRoleConsumer() {
    return roleRepository.findByRoleName(RoleEnum.CONSUMER)
        .orElseThrow(() -> new RuntimeException("Role not found"));
  }
  public void checkUserExists(String username) {
     var user = userRepository.findByUserName(username);
     if (user.isPresent()) {
       throw  new StudyHardException(ExceptionEnum.USERNAME_ALREADY_EXISTS);
     }
  }
  public User saveUserFromUserRegisterDto(UserRegisterRequest request){
    User user=User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()) )
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
  public void saveUserRoleFromUserRegisterDto(Role role,User user){
    UserRole userRole=UserRole.builder()
        .user(user)
        .role(role)
        .createAt(Instant.now())
        .updateAt(Instant.now())
        .build();
    userRoleRepository.save(userRole);
  }
}
