package com.studyhard.application.config;

import com.studyhard.application.dto.request.SupportTicketRequest;
import com.studyhard.application.entity.SupportTicket;
import com.studyhard.application.model.SupportTicketStatus;
import com.studyhard.application.repository.SupportTicketRepository;
import com.studyhard.application.service.RequestSupportTicketService;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class ToolCallingConfigForSupportTicket {

  RequestSupportTicketService requestSupportTicketService;
  private final SupportTicketRepository supportTicketRepository;

  @Tool(description = "Create support ticket")
  public String createSupportTicket(
      @ToolParam(description = "Field with support ticket") SupportTicketRequest request,
      ToolContext toolContext) {
    Long userId = (Long) toolContext.getContext().get("userId");
    SupportTicket supportTicket = SupportTicket.builder()
        .createAt(Instant.now())
        .status(SupportTicketStatus.Received)
        .issue(request.getIssue())
        .userId(userId)
        .build();
    log.info("Creating support ticket");
    supportTicketRepository.save(supportTicket);
    return "Create support ticket successfully";
  }

  @Tool(description = "Check issue exist")
  public List<SupportTicket> checkExistTicket(ToolContext toolContext) {
    Long userId = (Long) toolContext.getContext().get("userId");
    return supportTicketRepository.findByUserIdAndStatusIn(userId, List.of(SupportTicketStatus.Received,SupportTicketStatus.Processing));
  }
}
