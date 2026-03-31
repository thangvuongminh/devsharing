package com.studyhard.application.config;

import com.studyhard.application.dto.request.SupportTicketRequest;
import com.studyhard.application.dto.response.SupportTicketResponse;
import com.studyhard.application.entity.Role;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.UserRole;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.model.RoleEnum;
import com.studyhard.application.model.SupportTicketStatus;
import com.studyhard.application.mongo.entity.SupportTicket;
import com.studyhard.application.mongo.repository.SupportTicketRepository;
import com.studyhard.application.repository.RoleRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.UserRoleRepository;
import com.studyhard.application.utils.UserExtractor;
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
  SupportTicketRepository supportTicketRepository;
  @Tool(description = "create support ticket")
  @Transactional
  public String createSupportTicket(@ToolParam(description = "Object for create support ticket")SupportTicketRequest request, ToolContext toolContext)  {
    Long userId=Long.valueOf((String) toolContext.getContext().get("id"));
    SupportTicket supportTicket = SupportTicket.builder()
        .status(SupportTicketStatus.Received)
        .createdAt(Instant.now())
        .title(request.getTitle())
        .subject(request.getSubject())
        .userId(userId)
        .build();
    supportTicketRepository.save(supportTicket);
    return "Đã tạo ticket thành công với tiêu đề: " + supportTicket.getTitle();
  }
  @Tool(description = "check exist support ticket")
  @Transactional
  public Boolean checkSupportTicketPending(ToolContext toolContext) {
    Long userId=Long.valueOf((String) toolContext.getContext().get("id"));
    SupportTicket supportTicket=supportTicketRepository.findByUserIdAndStatus(userId,SupportTicketStatus.Processing);
    if(supportTicket!=null){
      return true;
    }
    return false;
  }

}
