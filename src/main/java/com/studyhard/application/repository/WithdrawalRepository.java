package com.studyhard.application.repository;

import com.studyhard.application.entity.WithdrawalRequest;
import com.studyhard.application.model.WithdrawalStatus;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository extends CrudRepository<WithdrawalRequest,Long>,
    JpaSpecificationExecutor<WithdrawalRequest> {
  Optional<WithdrawalRequest> findByUserId(Long userId);
  Optional<WithdrawalRequest> findByUserIdAndStatus(Long userId, WithdrawalStatus status);
  Page<WithdrawalRequest> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
  Page<WithdrawalRequest> findByStatusOrderByCreatedAtAsc(WithdrawalStatus status, Pageable pageable);

}