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
  USERNAME_NOT_FOUND("USERNAME_NOT_FOUND","Username not found. Please check your input or create a new account.",HttpStatus.BAD_REQUEST),
  USERNAME_ALREADY_EXISTS("USERNAME_EXISTS","The username you entered is already in use. Please try a different one.",HttpStatus.BAD_REQUEST),
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
