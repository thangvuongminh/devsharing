package com.studyhard.application.dto;

import com.studyhard.application.model.ContentLevel;
import com.studyhard.application.model.ContentStatus;
import java.math.BigDecimal;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentSummaryDto {
  Long id;
  String title;
  String description;
  Long creatorId;
  CategoryDto category;
  ContentStatus status;
  ContentLevel level;
  BigDecimal price;
  String thumbnail;
  Long viewCount;
  Long purchaseCount;
  Instant publishedAt;
  Instant createdAt;
}
