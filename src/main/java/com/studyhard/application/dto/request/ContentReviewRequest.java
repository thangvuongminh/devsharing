package com.studyhard.application.dto.request;

import com.studyhard.application.model.ContentStatus;
import com.studyhard.application.model.ReviewAction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ContentReviewRequest {
  String adminNote;
  Long contentId;
  ReviewAction  reviewAction;
}