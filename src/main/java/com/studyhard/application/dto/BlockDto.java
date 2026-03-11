package com.studyhard.application.dto;

import com.studyhard.application.model.BlockType;
import java.time.Instant;
import java.util.List;

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
