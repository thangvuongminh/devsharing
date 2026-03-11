package com.studyhard.application.service;

import com.studyhard.application.dto.WithdrawalRequestDto;
import com.studyhard.application.dto.request.ReviewWithdrawalRequest;
import com.studyhard.application.entity.WithdrawalRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WithdrawalService {
  public WithdrawalRequest createWithdrawalRequest(WithdrawalRequestDto withdrawalRequestDto);
  public Page<WithdrawalRequest> getUserWithdrawalRequests(Pageable pageable);
  public WithdrawalRequest getWithdrawalRequest(Long requestId);
  public Page<WithdrawalRequest> getPendingWithdrawalRequests(Pageable pageable);
  public WithdrawalRequest reviewWithdrawalRequest(Long withdrawalId,
      ReviewWithdrawalRequest reviewWithdrawalRequest);
}