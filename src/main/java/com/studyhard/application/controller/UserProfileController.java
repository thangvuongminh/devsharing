package com.studyhard.application.controller;

import com.studyhard.application.dto.request.UploadAvatarRequest;
import com.studyhard.application.dto.request.UploadImageRequest;
import com.studyhard.application.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("profile")
public class UserProfileController {

  @GetMapping("/upload/avatar")
  public ResponseEntity<ApiResponse<Void>> uploadAvatar(@Valid @ModelAttribute UploadAvatarRequest uploadAvatarRequest) {

  }
}
