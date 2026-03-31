package com.studyhard.application.mongo.repository;

import com.studyhard.application.model.SupportTicketStatus;
import com.studyhard.application.mongo.entity.SupportTicket;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketRepository  extends MongoRepository<SupportTicket, String> {

  List<SupportTicket> findByUserIdOrderByCreatedAtDesc(Long userId);

  SupportTicket findByUserIdAndStatus(Long userId, SupportTicketStatus status);
}
