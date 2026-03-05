package com.studyhard.application.exception;

import com.studyhard.application.response.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalHandlerException {
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        ApiResponse.error("An unexpected error occurred. Please refresh and try again.","INTERNAL_SERVER_ERROR")
    );
  }
  @ExceptionHandler(StudyHardException.class)
  public ResponseEntity<ApiResponse<?>> handleStudyHardException(StudyHardException e) {
    ExceptionEnum  exceptionEnum=(ExceptionEnum)e.getInfo();
    return ResponseEntity.status(exceptionEnum.getHttpStatus()).body(
        ApiResponse.error(exceptionEnum.getErrorMessage(),exceptionEnum.getErrorCode())
    );
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getFieldErrors().forEach((error)->{
      errors.put(error.getField(), error.getDefaultMessage());
    });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.success(errors));
  }
}
