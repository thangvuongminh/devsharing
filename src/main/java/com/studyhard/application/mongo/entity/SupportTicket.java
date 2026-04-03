package com.studyhard.application.mongo.entity;

import com.studyhard.application.model.SupportTicketStatus;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "supportTicket")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicket {
  @Id
  String id;
  @Field(name = "user_id")
  Long userId;
  @Field(name = "create_at")
  Instant createdAt;
  SupportTicketStatus status;
  @Field(name = "handle_by_user_id")
  Long handleByUserId;
  @Builder.Default
  List<ChatMessage> messages= new ArrayList<>();
}
