package com.studyhard.application.dto.response;

import com.studyhard.application.model.RoleEnum;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackMessageResponse {
  RoleEnum role;
  String content;
  Instant sentAt;
}