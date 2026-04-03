package com.studyhard.application.service;

import com.studyhard.application.dto.request.ChatMessageRequest;
import com.studyhard.application.dto.response.SupportTicketResponse;
import com.studyhard.application.mongo.entity.ChatMessage;
import com.studyhard.application.mongo.entity.SupportTicket;
import java.util.List;

public interface SupportTicketService  {
  public SupportTicketResponse createSupportTicket(ChatMessageRequest request);
  public List<SupportTicketResponse> getAllSupportTicket();
  public SupportTicketResponse getSupportTicketById(String ticketId);
  public  void chatSupport(ChatMessageRequest request,String ticketId,Long userId);
  public Boolean checkRequestModerator(ChatMessage chatMessage, SupportTicket supportTicket);
  public void moderatorJoin(String ticketId,Long userId);
}
