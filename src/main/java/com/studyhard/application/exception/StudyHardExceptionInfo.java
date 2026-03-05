package com.studyhard.application.exception;

import org.springframework.http.HttpStatus;

public interface StudyHardExceptionInfo {
  public String getErrorCode();
  public String getErrorMessage();
  public HttpStatus getHttpStatus();
}
