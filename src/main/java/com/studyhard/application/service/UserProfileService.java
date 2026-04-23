package com.studyhard.application.service;

import com.studyhard.application.dto.ProfileDto;
import com.studyhard.application.dto.request.UploadAvatarRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
  public String  uploadAvatar(UploadAvatarRequest request);
  public String getAvatar();
  public ProfileDto updateProfile(ProfileDto request);
  public  ProfileDto getProfile();
  public  ProfileDto getProfileByNickName(String nickName);
  public  String getNickname();
}
