package com.studyhard.application.service;

import com.studyhard.application.dto.request.SupportTicketRequest;
import com.studyhard.application.dto.response.SupportTicketResponse;
import com.studyhard.application.model.SupportTicketStatus;
import com.studyhard.application.mongo.entity.SupportTicket;
import java.util.List;

public interface SupportTicketService  {
  public String createSupportTicket(SupportTicketRequest request);
  public String feedBackSupportTicket(String ticketId, String message);
  public List<SupportTicketResponse> getAllSupportTicket();
}
