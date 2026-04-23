package com.studyhard.application.mapper;

import com.studyhard.application.dto.ProfileDto;
import com.studyhard.application.dto.request.UserRegisterRequest;
import com.studyhard.application.dto.response.UserLoginResponse;
import com.studyhard.application.dto.response.UserRegistrationResponse;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.context.annotation.Profile;

@Mapper(componentModel = "spring",unmappedSourcePolicy = ReportingPolicy.IGNORE,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
  User toUser(UserRegisterRequest userRegisterDto);
  UserRegistrationResponse toUserRegistrationResponse(User user);
  @Mapping(source = "profile.user.id",target = "userId")
  ProfileDto toProfileDto(UserProfile profile);
}
