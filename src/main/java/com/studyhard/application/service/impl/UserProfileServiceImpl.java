package com.studyhard.application.service.impl;

import com.studyhard.application.dto.request.UploadAvatarRequest;
import com.studyhard.application.entity.UserProfile;
import com.studyhard.application.model.TypeFile;
import com.studyhard.application.repository.UserProfileRepository;
import com.studyhard.application.service.UserProfileService;
import com.studyhard.application.utils.UserExtractor;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class UserProfileServiceImpl implements UserProfileService {
  CloudinaryServiceImpl cloudinaryService;
  UserProfileRepository userProfileRepository;
  @Override
  public String uploadImage(UploadAvatarRequest request) {
    String public_id=cloudinaryService.saveFile(request.getFile(), TypeFile.AVATAR);
    String linkImage=cloudinaryService.getImage(public_id,false);
    Long userId= UserExtractor.getUserId();
    UserProfile userProfile=userProfileRepository.findByUserId(userId);
    userProfile.setAvatar(linkImage);
    userProfileRepository.save(userProfile);
    return "Upload avatar Success";
  }

  @Override
  public String getAvatar() {
    Long userId= UserExtractor.getUserId();
    UserProfile userProfile=userProfileRepository.findByUserId(userId);
    return userProfile.getAvatar();
  }
}
