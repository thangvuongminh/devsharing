package com.studyhard.application.service;

import com.studyhard.application.dto.request.UploadAvatarRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
  public String  uploadImage(UploadAvatarRequest request);
}
