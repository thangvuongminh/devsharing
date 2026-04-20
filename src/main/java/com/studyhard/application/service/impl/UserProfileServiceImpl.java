package com.studyhard.application.service.impl;

import com.studyhard.application.dto.request.UploadAvatarRequest;
import com.studyhard.application.model.TypeFile;
import com.studyhard.application.repository.UserProfileRepository;
import com.studyhard.application.service.UserProfileService;
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
    List<String> public_id=cloudinaryService.saveFile(List.of(request.getFile()), TypeFile.AVATAR);
    List<String> linkImage=cloudinaryService.getImage(public_id,false);
    return "";
  }
}
