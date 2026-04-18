package com.studyhard.application.exception;

import com.studyhard.application.response.ApiResponse;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalHandlerException {
  MessageSource messageSource;
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
    String message=messageSource.getMessage(exceptionEnum.getErrorCode(),null, LocaleContextHolder.getLocale());
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
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadCredentialsException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Username or password not match","Bad credentials!"));
  }
  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ApiResponse<?>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(null,"UNAUTHORIZED"));
  }
  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<ApiResponse<?>> handlePropertyReferenceException(PropertyReferenceException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(null,"FILED_DATA_NOT_MATCH"));
  }
  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<ApiResponse<?>> handleHttpClientErrorException(HttpClientErrorException e) {
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(null,"CODE NOT VALID"));
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(null,"REQUEST NOT VALID"));
  }
  @ExceptionHandler(InvalidBearerTokenException.class)
  public ResponseEntity<ApiResponse<?>> handleInvalidBearerTokenException(InvalidBearerTokenException e) {
    e.printStackTrace();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(null,"UNAUTHORIZED"));
  }
}
