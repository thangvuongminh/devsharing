package com.studyhard.application.dto.request;

import com.studyhard.application.utils.validate.ValidFileExtension;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadAvatarRequest {
  @ValidFileExtension(message = "File not valid")
  MultipartFile file;
}
