package com.studyhard.application.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ApiResponse<T> {
  ResponseStatus status;
  T data;
  String message;
  String errorCode;
  public static <T> ApiResponse<T> success(T data) {
     return ApiResponse.<T>builder()
         .status(ResponseStatus.SUCCESS)
         .data(data)
         .build();
  }
  public static <T> ApiResponse<T> success(T data,String message) {
    return ApiResponse.<T>builder()
        .status(ResponseStatus.SUCCESS)
        .data(data)
        .message(message)
        .build();
  }
  public static <T> ApiResponse<T> error(T data,String message,String errorCode) {
    return ApiResponse.<T>builder()
        .status(ResponseStatus.ERROR)
        .data(data)
        .message(message)
        .errorCode(errorCode)
        .build();
  }
  public static <T> ApiResponse<T> error(String message,String errorCode) {
    return ApiResponse.<T>builder()
        .status(ResponseStatus.ERROR)
        .message(message)
        .errorCode(errorCode)
        .build();
  }
}
