package com.studyhard.application.service;

import com.studyhard.application.dto.request.ChangePasswordRequest;
import com.studyhard.application.dto.request.ResetPasswordRequest;
import com.studyhard.application.dto.request.UserLoginRequest;
import com.studyhard.application.dto.request.UserRegisterRequest;
import com.studyhard.application.dto.response.UserLoginResponse;
import com.studyhard.application.dto.response.UserRegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;


public interface UserAccountService {

  public UserRegistrationResponse registerUser(UserRegisterRequest userRegisterRequest);

  public UserLoginResponse loginUser(UserLoginRequest userLoginRequest);

  public void logoutUser(HttpServletResponse response);

  public UserLoginResponse refreshToken(HttpServletRequest request);

  public void forgotPassword(String email);

  public  void resetPassword(ResetPasswordRequest resetPasswordRequest);

  public void changePassword(ChangePasswordRequest changePasswordRequest);

  public  UserLoginResponse loginByGoogle(String accessToken);
}
