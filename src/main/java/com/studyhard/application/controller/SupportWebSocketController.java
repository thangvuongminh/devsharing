package com.studyhard.application.controller;

import com.studyhard.application.dto.request.ChatMessageRequest;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.mongo.entity.ChatMessage;
import com.studyhard.application.mongo.entity.SupportTicket;
import com.studyhard.application.service.BeginCreatorService;
import com.studyhard.application.service.SupportTicketService;
import java.security.Principal;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class SupportWebSocketController {

  SupportTicketService supportTicketService;
  BeginCreatorService beginCreatorService;
  @MessageMapping("chat/support/{ticketId}")
  @SendTo("/queue/support/{ticketId}")
  public void chatSupport(@Payload ChatMessageRequest request, @DestinationVariable String ticketId,
      Principal principal) {
    Long userId = Long.valueOf(principal.getName());
    supportTicketService.chatSupport(request, ticketId, userId);
  }

  @MessageMapping("chat/support/{ticketId}/moderator/join")
  @SendTo("/queue/support/{ticketId}")
  public void moderatorJoin(@DestinationVariable String ticketId, Principal principal) {
    Long userId = Long.valueOf(principal.getName());
    supportTicketService.moderatorJoin(ticketId, userId);
  }

  @MessageMapping("chat/support/creator")
  @SendTo("/queue/support/{ticketId}")
  public void becomeCreator(Principal principal) {
    Long userId = Long.valueOf(principal.getName());
  }

  @MessageExceptionHandler(StudyHardException.class)
  @SendToUser("/queue/errors")
  public String handleStudyHardException(StudyHardException ex) {
    return "Lỗi: " + ex.getInfo().getErrorMessage();
  }
}
