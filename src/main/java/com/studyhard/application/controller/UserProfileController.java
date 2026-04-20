package com.studyhard.application.controller;

import com.studyhard.application.dto.request.UploadAvatarRequest;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("profile")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class UserProfileController {
  UserProfileService userProfileService;
  @PostMapping("/upload/avatar")
  public ResponseEntity<ApiResponse<String>> uploadAvatar(@Valid @ModelAttribute UploadAvatarRequest uploadAvatarRequest) {
    String response =userProfileService.uploadImage(uploadAvatarRequest);
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
  @GetMapping("/get/avatar")
  public ResponseEntity<ApiResponse<String>> getAvatar() {
    String response =userProfileService.getAvatar();
    if(response==null){
      response="User not uploadAvatar";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.success(response));
    }
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
}
