package com.studyhard.application.controller;

import com.studyhard.application.dto.request.UserLoginRequest;
import com.studyhard.application.dto.request.UserRegisterRequest;
import com.studyhard.application.dto.response.UserRegistrationResponse;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/account")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Tag(name = "User account")
public class UserAccountController {
  UserAccountService userAccountService;
  @PostMapping("/register")
  @Operation(summary = "Register a new user", description = "Creates a new user account in the system." )
  public ResponseEntity<ApiResponse<UserRegistrationResponse>> registerUser(
    @Valid @RequestBody UserRegisterRequest request
  ) {
     UserRegistrationResponse response= userAccountService.registerUser(request);
    return ResponseEntity.ok(ApiResponse.success(response)) ;
  }
  @PostMapping("/login")
  @Operation(summary = "User login", description = "Authenticates a user using email and password credentials." )
  public ResponseEntity<ApiResponse<Void>> loginUser(
       @RequestBody UserLoginRequest request
  ) {
    userAccountService.loginUser(request);
    return ResponseEntity.ok(ApiResponse.success(null)) ;
  }
}