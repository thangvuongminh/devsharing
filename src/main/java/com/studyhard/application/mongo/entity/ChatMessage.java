package com.studyhard.application.mongo.entity;

import jakarta.persistence.Id;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {

  @Id
  String id;

  Long userId;        // ai gửi
  String content;       // nội dung
  MessageType type;     // USER, AI
  Instant sentAt; // thời gian
  public String toString() {
    return "content: " + content + " type: " + type.name();
  }
}
