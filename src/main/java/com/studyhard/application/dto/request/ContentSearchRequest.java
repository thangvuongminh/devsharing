package com.studyhard.application.dto.request;

import com.studyhard.application.model.ContentLevel;
import com.studyhard.application.model.ContentStatus;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentSearchRequest {

  String keyword; // Search in title and description
  Long categoryId;
  ContentStatus status;
  ContentLevel level;
  Long creatorId;
  BigDecimal minPrice;
  BigDecimal maxPrice;
  Long minViewCount;
  Long maxViewCount;
  Long minPurchaseCount;
  Long maxPurchaseCount;
}