package com.studyhard.application.repository;

import com.studyhard.application.entity.SupportTicket;
import com.studyhard.application.model.SupportTicketStatus;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketRepository extends CrudRepository<SupportTicket, Long> {

  @Query("SELECT st FROM SupportTicket st WHERE st.userId = :userId  AND st.status IN (:status)")
  List<SupportTicket> findByUserIdAndStatusIn(Long userId,List<SupportTicketStatus> status);
}
