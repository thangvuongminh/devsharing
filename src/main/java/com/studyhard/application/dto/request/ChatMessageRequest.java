package com.studyhard.application.dto.request;

import com.studyhard.application.mongo.entity.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageRequest {

  @NotBlank(message = "Content không được để trống")
  @Size(max = 1000, message = "Content tối đa 1000 ký tự")
  String content;
}