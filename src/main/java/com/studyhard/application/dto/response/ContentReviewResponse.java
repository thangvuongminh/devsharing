package com.studyhard.application.dto.response;

import com.studyhard.application.dto.ContentDto;
import com.studyhard.application.entity.Content;
import com.studyhard.application.model.ReviewAction;
import java.security.Timestamp;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentReviewResponse {
  Long id;
  ReviewAction action;
  String feedback;
  Instant actionAt;
  ContentDto content;
}
