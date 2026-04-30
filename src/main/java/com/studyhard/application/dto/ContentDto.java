package com.studyhard.application.dto;
import com.studyhard.application.entity.Block;
import com.studyhard.application.model.ContentLevel;
import com.studyhard.application.model.ContentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentDto {
  Long id;
  String title;
  String description;
  ContentLevel contentLevel;
  Long creatorId;
  String nickname;
  String urlAvatarAuthor;
  String thumb;
  CategoryDto category;
  BigDecimal price;
  ContentStatus status;
  ContentLevel level;
  Long viewCount;
  Long purchaseCount;
  Instant publishedAt;
  Instant createdAt;
  Instant updatedAt;
  List<BlockDto> blocks;
}