package com.studyhard.application.repository;

import com.studyhard.application.entity.Transaction;
import com.studyhard.application.model.TransactionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository  extends JpaRepository<Transaction,Long> {

  Transaction findTransactionByUserIdAndTypeAndReferenceId(Long userId, TransactionType type, String referenceId);

  Optional<Transaction> findByReferenceId(String referenceId);
}
