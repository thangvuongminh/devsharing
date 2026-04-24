package com.studyhard.application.tool;

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
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
@RequiredArgsConstructor
public class RegisterCreator {
  UserRoleRepository userRoleRepository;
  UserRepository userRepository;
  RoleRepository roleRepository;
  @Tool(description = """
    Check whether the user has already registered as a creator.
    ALWAYS call this tool FIRST before calling createCreatorRole.
    Return true if already a creator (stop, do not proceed).
    Return false if not yet a creator (can proceed to createCreatorRole).
    """)
  public  Boolean checkUserRegisterCreator(ToolContext toolContext){
    Long userId=(Long) toolContext.getContext().get("userId");
    List<UserRole> userRole=userRoleRepository.findByUserId(userId);
    for(UserRole ur:userRole){
      if (ur.getRole().getRoleName().equals(RoleEnum.CREATOR)){
        return true;
      }
    }
    return false;
  }
  @Tool(description = """
    Create a creator role for approved users.
    IMPORTANT: Only call this tool if checkUserRegisterCreator returns false.
    Do NOT call this tool if checkUserRegisterCreator returns true.
    """)
  public String createCreatorRole(ToolContext toolContext){
    Long userId=(Long) toolContext.getContext().get("userId");
    Role role=roleRepository.findByRoleName(RoleEnum.CREATOR).get();
    User user=userRepository.findById(userId).orElseThrow(() -> new StudyHardException(
        ExceptionEnum.USERNAME_NOT_FOUND));
    UserRole userRole=userRoleRepository.findByUserAndRole(user,role);
    if(userRole!=null){
      return "ALREADY_CREATOR";
    }
    userRole= UserRole.builder()
        .createdAt(Instant.now())
        .user(user)
        .role(role)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
    userRoleRepository.save(userRole);
    return  "success";
  }
}
