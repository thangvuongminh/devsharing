package com.studyhard.application.dto;

import com.studyhard.application.dto.request.ContentPreviewRequest;
import com.studyhard.application.model.ContentStatus;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ContentPreviewDto {
  ContentStatus contentStatus;
  ContentPreviewRequest contentPreviewRequest;
}
