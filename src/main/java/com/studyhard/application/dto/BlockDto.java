package com.studyhard.application.dto;

import com.studyhard.application.model.BlockType;
import java.time.Instant;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlockDto {
  Long id;
  Long contentId;
  Long parentBlockId;
  BlockType type;
  String textContent;
  String properties; // JSON string
  Integer position;
  Boolean isFree;
  Instant createdAt;
  Instant updatedAt;
  List<BlockDto> children;
}
