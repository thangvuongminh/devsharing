package com.studyhard.application.controller;

import com.studyhard.application.dto.request.SupportTicketRequest;
import com.studyhard.application.dto.response.SupportTicketResponse;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("support/customer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupportCustomerController {
  SupportTicketService supportTicketService;
  @PostMapping("create")
  @Operation(description = "supportTicketMapper")
  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
  public ResponseEntity<ApiResponse<String>> createSupportTicketResponse(
      SupportTicketRequest  request) {
    String supportTicketResponse=supportTicketService.createSupportTicket(request);
    return  ResponseEntity.ok().body(ApiResponse.success(supportTicketResponse));
  }
  @PostMapping("{ticketId}/feedback")
  @Operation(description = "supportTicketMapper")
  @PreAuthorize("hasAnyRole('CONSUMER','MODERATOR')")
  public ResponseEntity<ApiResponse<String>> feedBackSupportTicket(
     @PathVariable String ticketId, String message) {
    String response=supportTicketService.feedBackSupportTicket(ticketId,message);
    return  ResponseEntity.ok().body(ApiResponse.success(response));
  }
  @GetMapping("/all")
  @Operation(description = "get all support ticket")
  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
  public ResponseEntity<ApiResponse<List<SupportTicketResponse>>> getAllSupportTicket(
      ) {
    List<SupportTicketResponse> response=supportTicketService.getAllSupportTicket();
    return  ResponseEntity.ok().body(ApiResponse.success(response));
  }
}
