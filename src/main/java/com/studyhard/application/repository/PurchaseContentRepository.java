package com.studyhard.application.repository;

import com.studyhard.application.entity.PurchaseContent;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseContentRepository extends CrudRepository<PurchaseContent,Long> {
  Optional<PurchaseContent> findByContentIdAndUserId(Long contentId,Long userId);
}
