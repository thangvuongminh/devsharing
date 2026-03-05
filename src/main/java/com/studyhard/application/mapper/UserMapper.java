package com.studyhard.application.mapper;

import com.studyhard.application.dto.request.UserRegisterRequest;
import com.studyhard.application.dto.response.UserRegistrationResponse;
import com.studyhard.application.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedSourcePolicy = ReportingPolicy.IGNORE,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
  User toUser(UserRegisterRequest userRegisterDto);
  UserRegistrationResponse toUserRegistrationResponse(User user);
}
