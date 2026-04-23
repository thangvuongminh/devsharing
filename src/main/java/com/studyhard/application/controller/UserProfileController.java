package com.studyhard.application.controller;

import com.studyhard.application.dto.ProfileDto;
import com.studyhard.application.dto.request.UploadAvatarRequest;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  @PostMapping(value = "/upload/avatar",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "User upload avatar")
  @PreAuthorize("hasAnyRole('CREATOR')")
  public ResponseEntity<ApiResponse<String>> uploadAvatar(@Valid @ModelAttribute UploadAvatarRequest uploadAvatarRequest) {
    String response =userProfileService.uploadAvatar(uploadAvatarRequest);
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
  @GetMapping("/get/avatar")
  @PreAuthorize("hasAnyRole('CREATOR')")
  public ResponseEntity<ApiResponse<String>> getAvatar() {
    String response =userProfileService.getAvatar();
    if(response==null){
      response="User not uploadAvatar";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.success(response));
    }
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
  @DeleteMapping("/delete/avatar")
  @PreAuthorize("hasAnyRole('CREATOR')")
  public ResponseEntity<ApiResponse<String>> deleteAvatar(@Valid @ModelAttribute UploadAvatarRequest uploadAvatarRequest) {
    String response =userProfileService.getAvatar();
    if(response==null){
      response="User not uploadAvatar";
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.success(response));
    }
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }

  @PutMapping("/update")
  @PreAuthorize("hasAnyRole('CREATOR')")
  public ResponseEntity<ApiResponse<ProfileDto>> updateProfile(@Valid @RequestBody ProfileDto profileDto) {
     ProfileDto response= userProfileService.updateProfile(profileDto);
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
  @GetMapping("/get/nickname")
  @PreAuthorize("hasAnyRole('CREATOR')")
  public ResponseEntity<ApiResponse<String>> getNickname() {
    String response= userProfileService.getNickname();
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
  @GetMapping("/nickname/{nickName}")
  public ResponseEntity<ApiResponse<ProfileDto>> getProfile(@PathVariable String nickName) {
    ProfileDto response= userProfileService.getProfileByNickName(nickName);
    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
}
