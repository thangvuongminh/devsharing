package com.studyhard.application.dto.response;

import com.studyhard.application.model.ReviewAction;
import java.security.Timestamp;
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
  Long contentId;
  ReviewAction reviewAction;
  String feedBack;
  Timestamp action_at;
}
