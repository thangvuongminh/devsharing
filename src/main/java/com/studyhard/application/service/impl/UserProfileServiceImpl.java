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
    String public_id = cloudinaryService.saveFile(request.getAvatar(), TypeFile.AVATAR);
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
  public  Boolean checkExistNickname(String nickname){
    UserProfile userProfile= userProfileRepository.findUserByNickName(nickname);
    return userProfile != null;
  }
  @Override
  public ProfileDto updateProfile(ProfileDto request) {
    UserProfile userProfile=getOrCreateProfile(UserExtractor.getUserId());
    if(request.getFullName()!=null){
      userProfile.setFullName(request.getFullName());
    }
    if(request.getNickName()!=null){
      if(checkExistNickname(request.getNickName())){
        throw  new StudyHardException(ExceptionEnum.NICK_NAME_ALREADY_EXIST);
      }
      userProfile.setNickName(request.getNickName());
    }
    if(request.getAddress() != null) {
      userProfile.setAddress(request.getAddress());
    }
    if(request.getGender() != null) {
      userProfile.setGender(request.getGender());
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
    if(userProfile.getAvatar()!=null){
      userProfile.setAvatar(cloudinaryService.getImage(userProfile.getAvatar(), false));
    }
    return   userMapper.toProfileDto(userProfile);
  }

  @Override
  public ProfileDto getProfileByNickName(String nickName) {
    if(!checkExistNickname(nickName)){
      throw  new StudyHardException(ExceptionEnum.NICK_NAME_NOT_FOUND);
    }
    UserProfile userProfile =userProfileRepository.findUserByNickName(nickName);
    return userMapper.toProfileDto(userProfile);
  }

  @Override
  public String getNickname() {
    UserProfile userProfile = getOrCreateProfile(UserExtractor.getUserId());
    return userProfile.getNickName();
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
