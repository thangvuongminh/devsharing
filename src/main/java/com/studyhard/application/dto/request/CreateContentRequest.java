package com.studyhard.application.dto.request;

import com.studyhard.application.model.ContentLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateContentRequest {
  @NotBlank(message = "{content.title.notBlank}")
  @Size(max = 255, message = "{content.title.size}")
  String title;
  @Size(max = 500, message = "{content.description.size}")
  String description;
  ContentLevel contentLevel;
  @NotNull(message = "{content.price.notNull}")
  @DecimalMin(value = "0.0",message = "{content.price.min}")
  BigDecimal price;
  @NotNull(message = "{content.thumb.notNull}")
  MultipartFile thumb;
  @NotNull(message = "{content.categoryId.notNull}")
  Long categoryId;
}