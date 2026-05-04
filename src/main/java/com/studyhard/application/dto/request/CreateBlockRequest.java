package com.studyhard.application.dto.request;

import com.studyhard.application.model.BlockType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class CreateBlockRequest {
  Long parentBlockId;
  String textContent;
  @NotNull(message = "{block.type.notnull}")
  BlockType type;
  @NotNull
  String title;
  String properties;
  @NotNull(message = "{block.position.notNull}")
  @Min(value = 1,message = "{block.position.min")
  Integer position;
  @Builder.Default
  Boolean isFree=false;
}