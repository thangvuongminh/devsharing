package com.studyhard.application.dto.response;

import com.studyhard.application.model.SupportTicketStatus;
import com.studyhard.application.mongo.entity.ChatMessage;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SupportTicketResponse {
  String id;
  String title;
  String subject;
  SupportTicketStatus status;
  Instant createdAt;
  Long handleByUserId;
  List<ChatMessage> messages;
}