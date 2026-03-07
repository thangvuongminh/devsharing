package com.studyhard.application.controller;

import com.studyhard.application.dto.request.ChangePasswordRequest;
import com.studyhard.application.dto.request.ForgotPasswordRequest;
import com.studyhard.application.dto.request.ResetPasswordRequest;
import com.studyhard.application.dto.request.UserLoginRequest;
import com.studyhard.application.dto.request.UserRegisterRequest;
import com.studyhard.application.dto.response.UserLoginResponse;
import com.studyhard.application.dto.response.UserRegistrationResponse;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user/account")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Tag(name = "User account")
public class UserAccountController {

  UserAccountService userAccountService;

  @PostMapping("/register")
  @Operation(summary = "Register a new user", description = "Creates a new user account in the system.")
  public ResponseEntity<ApiResponse<UserRegistrationResponse>> registerUser(
      @Valid @RequestBody UserRegisterRequest request
  ) {
    UserRegistrationResponse response = userAccountService.registerUser(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/login")
  @Operation(summary = "User login", description = "Authenticates a user using email and password credentials.")
  public ResponseEntity<ApiResponse<UserLoginResponse>> loginUser(
      @RequestBody UserLoginRequest request, HttpServletResponse response
  ) {
    UserLoginResponse userLoginResponse = userAccountService.loginUser(request);
    ResponseCookie resCookie = ResponseCookie.from("studyHard", userLoginResponse.getRefreshToken())
        .httpOnly(true)
        .secure(false)
        .path("/")
        .build();
    response.addHeader("Set-Cookie", resCookie.toString());
    return ResponseEntity.ok(ApiResponse.success(userLoginResponse));
  }

  @PutMapping("/logout")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<Void>> logoutUser(HttpServletResponse response) {
    userAccountService.logoutUser(response);
    return ResponseEntity.ok().body(ApiResponse.success(null));
  }

  @GetMapping("/refreshToken")
  public ResponseEntity<ApiResponse<UserLoginResponse>> refreshToken(HttpServletRequest request) {
    UserLoginResponse response = userAccountService.refreshToken(request);
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<ApiResponse<String>> forgotPassword(
      @RequestBody ForgotPasswordRequest request) {
    userAccountService.forgotPassword(request.getEmail());
    return ResponseEntity.ok().body(ApiResponse.success("Send email successfully!"));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse<String>> resetPassword(
      @RequestBody ResetPasswordRequest request) {
    userAccountService.resetPassword(request);
    return ResponseEntity.ok().body(ApiResponse.success("reset password successfully!"));
  }

  @PostMapping("/change-password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<String>> changePassword(
      @RequestBody ChangePasswordRequest request
  ){
    userAccountService.changePassword(request);
    return ResponseEntity.ok().body(ApiResponse.success("change password successfully!"));
  }
}