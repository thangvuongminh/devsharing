package com.studyhard.application.service;

import com.studyhard.application.dto.request.UserLoginRequest;
import com.studyhard.application.dto.request.UserRegisterRequest;
import com.studyhard.application.dto.response.UserRegistrationResponse;
import org.springframework.stereotype.Service;


public interface UserAccountService {
  public UserRegistrationResponse registerUser(UserRegisterRequest userRegisterRequest);
  public void loginUser(UserLoginRequest userLoginRequest);
}
