package com.studyhard.application.dto.response;

import com.studyhard.application.model.SupportTicketStatus;
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
  List<FeedbackMessageResponse> messages;
}