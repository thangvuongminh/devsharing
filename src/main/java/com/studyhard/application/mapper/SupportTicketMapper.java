package com.studyhard.application.mapper;

import com.studyhard.application.dto.response.SupportTicketResponse;
import com.studyhard.application.mongo.entity.SupportTicket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupportTicketMapper {
  SupportTicketResponse toSupportTicketResponse(SupportTicket request);
}
