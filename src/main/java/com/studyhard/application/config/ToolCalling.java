package com.studyhard.application.config;

import com.studyhard.application.entity.Role;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.UserRole;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.model.RoleEnum;
import com.studyhard.application.repository.RoleRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.UserRoleRepository;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class ToolCalling {
  UserRoleRepository userRoleRepository;
  UserRepository userRepository;
  RoleRepository roleRepository;
  @Tool(description = "assign_creator_role")
  @Transactional
  public void createUserRoleCreator(ToolContext toolContext) {
    Long userId= (Long) toolContext.getContext().get("userId");
    Role role=roleRepository.findByRoleName(RoleEnum.CREATOR).orElseThrow(() -> new StudyHardException(
        ExceptionEnum.ROLE_NOT_FOUND));
    User user = userRepository.findById(userId).orElseThrow(() -> new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND));
    UserRole userRole=UserRole.builder()
        .user(user)
        .role(role)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
    userRoleRepository.save(userRole);
  }
//  @Tool(description = "become_moderator")
//  @Transactional
//  public void becomeModerator(ToolContext toolContext) {
//
//  }

}
