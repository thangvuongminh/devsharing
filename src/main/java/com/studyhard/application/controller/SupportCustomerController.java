package com.studyhard.application.controller;

import com.studyhard.application.dto.request.ChatMessageRequest;
import com.studyhard.application.dto.response.SupportTicketResponse;
import com.studyhard.application.mongo.entity.ChatMessage;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.BeginCreatorService;
import com.studyhard.application.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("support/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupportCustomerController {

  SupportTicketService supportTicketService;
  BeginCreatorService beginCreatorService;
  @GetMapping("/all")
  @Operation(description = "get all support ticket")
  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
  public ResponseEntity<ApiResponse<List<SupportTicketResponse>>> getAllSupportTicket(
  ) {
    List<SupportTicketResponse> response = supportTicketService.getAllSupportTicket();
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }

  @PostMapping("create/ticket")
  @Operation(description = "created support ticket")
  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
  public ResponseEntity<ApiResponse<SupportTicketResponse>> createSupportTicket(
    @RequestBody ChatMessageRequest chatMessage) {
    SupportTicketResponse supportTicketResponse = supportTicketService.createSupportTicket(
        chatMessage);
    return ResponseEntity.ok().body(ApiResponse.success(supportTicketResponse));
  }
  @GetMapping("all/ticket")
  @Operation(description = "Get all ticket")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse<List<SupportTicketResponse>>> getAllSupportTicket(
      @RequestBody ChatMessageRequest chatMessage) {
    List<SupportTicketResponse> response = supportTicketService.getAllSupportTicket();
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
  @GetMapping("/upload/images")
  @Operation(description = "Begin creator")
  @PreAuthorize("hasRole('CONSUMER')")
  public ResponseEntity<ApiResponse<List<String>>> uploadImagesBeginCreator(
      @ModelAttribute List<MultipartFile> files) {
    List<String> response = beginCreatorService.uploadImagesBeginCreator(files);
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
//  @GetMapping("ticket/{ticketID")
//  @Operation(description = "Get ticketId")
//  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
//  public ResponseEntity<ApiResponse<SupportTicketResponse>> createSupportTicket(
//      @RequestBody ChatMessageRequest chatMessage) {
//    SupportTicketResponse supportTicketResponse = supportTicketService.createSupportTicket(
//        chatMessage);
//    return ResponseEntity.ok().body(ApiResponse.success(supportTicketResponse));
//  }
}
