package com.studyhard.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCategoryRequest {
  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name must not exceed 100 characters")
  String name;
  @NotBlank(message = "Description is required")
  @Size(max = 500, message = "Description must not exceed 100 characters")
  String description;
  @NotBlank(message = "Slug is required")
  @Size(max = 100, message = "Slug must not exceed 100 characters")
  String slug;
}