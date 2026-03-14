package com.studyhard.application.controller;

import com.google.protobuf.Api;
import com.studyhard.application.entity.SupportTicket;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.ChatAiService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("support")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestSupportTicketController {
  ChatAiService chatAiService;
  @GetMapping
  public ResponseEntity<ApiResponse<?>> helpChatClientSupport(@RequestParam String message) {
    String response=chatAiService.supportCustomer(message);
    return  ResponseEntity.ok(ApiResponse.success(response));
  }
}
