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
  REFRESH_TOKEN_MISSING("REFRESH_TOKEN_MISSING",
      "Refresh token is required to access this resource.", HttpStatus.UNAUTHORIZED),
  INVALID_TOKEN("INVALID_TOKEN", "Invalid token provided.", HttpStatus.UNAUTHORIZED),
  INVALID_OTP("INVALID_OTP", "Invalid or expired OTP.", HttpStatus.BAD_REQUEST),
  USERNAME_NOT_FOUND("USERNAME_NOT_FOUND",
      "Username not found. Please check your input or create a new account.",
      HttpStatus.BAD_REQUEST),
  USERNAME_ALREADY_EXISTS("USERNAME_EXISTS",
      "The username you entered is already in use. Please try a different one.",
      HttpStatus.BAD_REQUEST),
  EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS",
      "The email you entered is already in use. Please try a different one.",
      HttpStatus.BAD_REQUEST),
  PASSWORD_NOT_MATCH("PASSWORD_NOT_MATCH", "Passwords do not match. Please try again.",
      HttpStatus.BAD_REQUEST),

  // CONTENT_NOT_FOUND
  CONTENT_NOT_FOUND("CONTENT_NOT_FOUND", "Content not found.", HttpStatus.NOT_FOUND),
  UNAUTHORIZE_CONTENT_ACCESS("UNAUTHORIZE_CONTENT_ACCESS",
      "Access Denied. You do not have permission to view this content.", HttpStatus.UNAUTHORIZED),
  CONTENT_ALREADY_YOUR_CAR("CONTENT_ALREADY_YOUR_CAR", "Content is already in your cart.",
      HttpStatus.BAD_REQUEST),
  CONTENT_ALREADY_PUBLISHED("CONTENT_ALREADY_PUBLISHED", "Content is already published.",
      HttpStatus.BAD_REQUEST),
  CONTENT_REVIEW_OR_REJECTION("CONTENT_REVIEW_OR_REJECTION",
      "The content is either under review or has been rejected.published,premium.",
      HttpStatus.BAD_REQUEST),
  CONTENT_IS_PURCHASED("CONTENT_IS_PURCHASED", "Content has already been purchased",
      HttpStatus.BAD_REQUEST),

  CONTENT_IS_PREMIUM("CONTENT_IS_PREMIUM", "Content has been premium.", HttpStatus.BAD_REQUEST),

  // category
  CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "Requested category does not exist.",
      HttpStatus.BAD_REQUEST),

  // wallet
  MINIMUM_WITHDRAWAL_NOT_FULFILL("MINIMUM_WITHDRAWAL_NOT_FULFILL",
      "Withdrawal amount does not meet the minimum required of 10 credits. Please ensure the amount is above the minimum withdrawal limit.",
      HttpStatus.BAD_REQUEST),
  WALLET_NOT_FOUND("WALLET_NOT_FOUND", "Wallet not exist", HttpStatus.BAD_REQUEST),
  INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE",
      "Your balance is insufficient to complete this transaction", HttpStatus.BAD_REQUEST),
  PENDING_WITHDRAWAL_EXISTS("PENDING_WITHDRAWAL_EXISTS",
      "You have a pending withdrawal request. Please wait for the previous request to be processed before making another withdrawal.",
      HttpStatus.BAD_REQUEST),
  WITHDRAWAL_NOT_EXIST("WITHDRAWAL_NOT_EXIST", "Withdrawal not exist", HttpStatus.BAD_REQUEST),
  WITHDRAWAL_NOT_AUTHORIZE("WITHDRAWAL_NOT_AUTHORIZE",
      "You do not have permission to view this withdrawal request", HttpStatus.UNAUTHORIZED),
  WITHDRAWAL_ALREADY_PROCESSED("WITHDRAWAL_ALREADY_PROCESSED",
      "This error occurs when a withdrawal request has already been processed",
      HttpStatus.CONFLICT),


  // SUPPORT TICKET
  SUPPORT_TICKET_NOT_FOND("SUPPORT_TICKET_NOT_FOND","Support ticket not found",HttpStatus.BAD_REQUEST),
  SUPPORT_TICKET_IS_WAITING_RESPONSE("SUPPORT_TICKET_IS_WAITING_RESPONSE", "Support ticket is waiting for response", HttpStatus.BAD_REQUEST),
  // Content errors
  CONTENT_NO_REVIEW("CONTENT_NO_REVIEW", "Content must be submitted for moderator approval",
      HttpStatus.FORBIDDEN),
  CONTENT_CAN_NOT_SUMMIT("CONTENT_CAN_NOT_SUMMIT", "Content can not summit", HttpStatus.CONFLICT),
  SELF_PURCHASE_NOT_ALLOWED("SELF_PURCHASE_NOT_ALLOWED", "Self-purchase is not allowed",
      HttpStatus.CONFLICT),

  // Content Block
  BLOCK_NOT_FOUND("BLOCK_NOT_FOUND", "Block not found", HttpStatus.NOT_FOUND),
  PARENT_BLOCK_NOT_IN_SAME_CONTENT("PARENT_BLOCK_NOT_IN_SAME_CONTENT",
      "Parent and child blocks have mismatched content", HttpStatus.BAD_REQUEST),
  // ai
  INVALID_ANSWER("INVALID_ANSWER",
      "An error occurred while processing the AI response. Please try again.",
      HttpStatus.BAD_REQUEST),
  CONTENT_NOT_EXIST_IN_CART("CONTENT_NOT_EXIST_IN_CART",
      "The requested content does not exist in the cart.", HttpStatus.NOT_FOUND),
  // role

  ROLE_NOT_FOUND("ROLE_NOT_FOUND", "System role configuration error. Please contact administrator.",
      HttpStatus.INTERNAL_SERVER_ERROR);
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
