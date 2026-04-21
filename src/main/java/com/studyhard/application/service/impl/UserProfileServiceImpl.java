package com.studyhard.application.service.impl;

import com.studyhard.application.dto.ProfileDto;
import com.studyhard.application.dto.request.UploadAvatarRequest;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.UserProfile;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.mapper.UserMapper;
import com.studyhard.application.model.TypeFile;
import com.studyhard.application.repository.UserProfileRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.service.UserProfileService;
import com.studyhard.application.utils.UserExtractor;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileServiceImpl implements UserProfileService {

  CloudinaryServiceImpl cloudinaryService;
  UserProfileRepository userProfileRepository;
  UserRepository userRepository;
  UserMapper userMapper;
  @Override
  public String uploadAvatar(UploadAvatarRequest request) {
    String public_id = cloudinaryService.saveFile(request.getFile(), TypeFile.AVATAR);
    String linkImage = cloudinaryService.getImage(public_id, false);
    Long userId = UserExtractor.getUserId();
    UserProfile userProfile = getOrCreateProfile(userId);
    if (userProfile.getAvatar() != null) {
      cloudinaryService.deleteFile(TypeFile.AVATAR, List.of(userProfile.getAvatar()));
    }
    userProfile.setAvatar(public_id);
    userProfileRepository.save(userProfile);
    return "Upload avatar Success";
  }

  @Override
  public String getAvatar() {
    Long userId = UserExtractor.getUserId();
    UserProfile userProfile = getOrCreateProfile(userId);
    return cloudinaryService.getImage(userProfile.getAvatar(), false);
  }

  @Override
  public ProfileDto updateProfile(ProfileDto request) {
    UserProfile userProfile=getOrCreateProfile(UserExtractor.getUserId());
    if(request.getAddress() != null) {
      userProfile.setAddress(request.getAddress());
    }
    if (request.getBio() != null) {
      userProfile.setBio(request.getBio());
    }
    if (request.getCompany() != null) {
      userProfile.setCompany(request.getCompany());
    }
    if (request.getGender() != null) {
      userProfile.setGender(request.getGender());
    }
    if (request.getFacebook() != null) {
      userProfile.setFacebook(request.getFacebook());
    }
    if (request.getBirthDate() != null) {
      userProfile.setBirthDate(request.getBirthDate());
    }
    if (request.getEducationLevel() != null) {
      userProfile.setEducationLevel(request.getEducationLevel());
    }
    if (request.getPersonalWebsite() != null) {
      userProfile.setPersonalWebsite(request.getPersonalWebsite());
    }
    if (request.getYearOfExperience() != null) {
      userProfile.setYearOfExperience(request.getYearOfExperience());
    }
    userProfileRepository.save(userProfile);
    return userMapper.toProfileDto(userProfile);
  }

  @Override
  public ProfileDto getProfile() {
    UserProfile userProfile = getOrCreateProfile(UserExtractor.getUserId());
    return   userMapper.toProfileDto(userProfile);
  }

  public UserProfile getOrCreateProfile(Long userId) {
    return userProfileRepository.findByUserId(userId)
        .orElseGet(() -> {
          User user = userRepository.findById(userId)
              .orElseThrow(() -> new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND));
          UserProfile userProfile1 = UserProfile.builder()
              .user(user)
              .build();
          userRepository.save(user);
          return userProfile1;
        });
  }


}
