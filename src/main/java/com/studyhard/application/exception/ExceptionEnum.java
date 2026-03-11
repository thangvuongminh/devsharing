package com.studyhard.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum implements StudyHardExceptionInfo {
  REFRESH_TOKEN_MISSING("REFRESH_TOKEN_MISSING", "Refresh token is required to access this resource.",HttpStatus.UNAUTHORIZED),
  INVALID_TOKEN("INVALID_TOKEN", "Invalid token provided.",HttpStatus.UNAUTHORIZED),
  INVALID_OTP("INVALID_OTP", "Invalid or expired OTP.",HttpStatus.BAD_REQUEST),
  USERNAME_NOT_FOUND("USERNAME_NOT_FOUND","Username not found. Please check your input or create a new account.",HttpStatus.BAD_REQUEST),
  USERNAME_ALREADY_EXISTS("USERNAME_EXISTS","The username you entered is already in use. Please try a different one.",HttpStatus.BAD_REQUEST),
  EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS","The email you entered is already in use. Please try a different one.",HttpStatus.BAD_REQUEST),
  PASSWORD_NOT_MATCH("PASSWORD_NOT_MATCH", "Passwords do not match. Please try again.", HttpStatus.BAD_REQUEST),

  // CONTENT_NOT_FOUND
  CONTENT_NOT_FOUND("CONTENT_NOT_FOUND", "Content not found.", HttpStatus.NOT_FOUND),
  UNAUTHORIZE_CONTENT_ACCESS("UNAUTHORIZE_CONTENT_ACCESS", "Access Denied. You do not have permission to view this content.", HttpStatus.UNAUTHORIZED),
  CONTENT_ALREADY_YOUR_CAR("CONTENT_ALREADY_YOUR_CAR", "Content is already in your cart.", HttpStatus.BAD_REQUEST),
  CONTENT_REVIEW_OR_REJECTION("CONTENT_REVIEW_OR_REJECTION", "The content is either under review or has been rejected.", HttpStatus.BAD_REQUEST),
  // category
  CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "Requested category does not exist.", HttpStatus.BAD_REQUEST),
  ;
  String errorCode;
  String errorMessage;
  HttpStatus httpStatus;

  @Override
  public String getErrorCode() {
    return errorCode;
  }

  @Override
  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
