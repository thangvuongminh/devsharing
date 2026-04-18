package com.studyhard.application.mongo.entity;

import jakarta.persistence.Id;
import java.time.Instant;
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

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "begin_moderator")
public class BeginCreator {
  @Id
  String id;
  @Field(name = "user_id")
  Long userId;
  @Field(name = "created_at")
  Instant createdAt;
  @Field(name = "result")
  int result;
  String reason;
  List<ChatMessage> messages;
  @Field(name = "thumb_url")
  List<String> thumbUrl;
}
