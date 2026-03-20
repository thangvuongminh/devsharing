package com.studyhard.application.service.impl;

import com.studyhard.application.dto.WithdrawalRequestDto;
import com.studyhard.application.dto.request.ReviewWithdrawalRequest;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.Wallet;
import com.studyhard.application.entity.WithdrawalRequest;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.model.TransactionType;
import com.studyhard.application.model.WithdrawalStatus;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.WalletRepository;
import com.studyhard.application.repository.WithdrawalRepository;
import com.studyhard.application.service.WalletService;
import com.studyhard.application.service.WithdrawalService;
import com.studyhard.application.utils.UserExtractor;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WithdrawalServiceImpl implements WithdrawalService {
  BigDecimal MIN_WITHDRAWAL = new BigDecimal("10");
  WithdrawalRepository withdrawalRepository;
  WalletService walletService;
  WalletRepository walletRepository;
  UserRepository userRepository;
  @Override
  @Transactional
  public WithdrawalRequest createWithdrawalRequest(WithdrawalRequestDto withdrawalRequestDto) {
    Long userId = UserExtractor.getUserId();
    if (withdrawalRequestDto.getAmount().compareTo(MIN_WITHDRAWAL) <= 0) {
      throw new StudyHardException(ExceptionEnum.MINIMUM_WITHDRAWAL_NOT_FULFILL);
    }
    User user1 = userRepository.findById(userId).get();
    Wallet wallet = walletRepository.findByUserIdWithLock(user1).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.WALLET_NOT_FOUND)
    );
    if (wallet.getBalance().compareTo(withdrawalRequestDto.getAmount()) <= 0) {
      throw new StudyHardException(ExceptionEnum.INSUFFICIENT_BALANCE);
    }
    var withdrawalRequest = withdrawalRepository.findByUserIdAndStatus(userId,
        WithdrawalStatus.PENDING);
    if (withdrawalRequest.isPresent()) {
      throw new StudyHardException(ExceptionEnum.PENDING_WITHDRAWAL_EXISTS);
    }
    User user=userRepository.findById(UserExtractor.getUserId()).get();
    WithdrawalRequest withdrawalRequest1=WithdrawalRequest.builder()
        .createdAt(Instant.now())
        .user(user)
        .accountHolderName(withdrawalRequestDto.getAccountHolderName())
        .bankName(withdrawalRequestDto.getBankName())
        .amount(withdrawalRequestDto.getAmount())
        .bankAccountNumber(withdrawalRequestDto.getBankAccountNumber())
        .note(withdrawalRequestDto.getNote())
        .status(WithdrawalStatus.PENDING)
        .updatedAt(Instant.now())
        .build();
    withdrawalRepository.save(withdrawalRequest1);
    return withdrawalRequest1;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WithdrawalRequest> getUserWithdrawalRequests(Pageable pageable) {
    return withdrawalRepository.findByUserIdOrderByCreatedAtDesc(UserExtractor.getUserId(), pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public WithdrawalRequest getWithdrawalRequest(Long requestId) {
    WithdrawalRequest withdrawalRequest = withdrawalRepository.findById(requestId).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.WITHDRAWAL_NOT_EXIST, new Object[]{requestId})
    );
    User user=userRepository.findById(UserExtractor.getUserId()).get();
    if (!withdrawalRequest.getUser().equals(user)) {
      throw new StudyHardException(ExceptionEnum.WITHDRAWAL_NOT_AUTHORIZE);
    }
    return withdrawalRequest;
  }

  @Override
  @Transactional(readOnly = true)
  public Page<WithdrawalRequest> getPendingWithdrawalRequests(Pageable pageable) {
    return withdrawalRepository.findByStatusOrderByCreatedAtAsc(WithdrawalStatus.PENDING, pageable);
  }

  @Override
  public WithdrawalRequest reviewWithdrawalRequest( Long withdrawalId,
      ReviewWithdrawalRequest reviewWithdrawalRequest) {
    WithdrawalRequest withdrawalRequest = withdrawalRepository.findById(withdrawalId).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.WITHDRAWAL_NOT_EXIST, new Object[]{withdrawalId})
    );
    if(!withdrawalRequest.getStatus().equals(WithdrawalStatus.PENDING)) {
      throw new StudyHardException(ExceptionEnum.WITHDRAWAL_ALREADY_PROCESSED);
    }
    User user=userRepository.findById(UserExtractor.getUserId()).get();
    if (reviewWithdrawalRequest.getApproved()){
      walletService.deductCredit(withdrawalRequest.getUser(),withdrawalRequest.getAmount(),
          TransactionType.WITHDRAW,"Withdrawal approve " + reviewWithdrawalRequest.getAdminNote(), String.valueOf(withdrawalId));
      withdrawalRequest.setStatus(WithdrawalStatus.APPROVE);
    }else {
      withdrawalRequest.setStatus(WithdrawalStatus.REJECT);
    }
    withdrawalRequest.setReviewedAt(Instant.now());
    withdrawalRequest.setAdminNote(reviewWithdrawalRequest.getAdminNote());
    withdrawalRequest.setReviewer(user);
    withdrawalRequest.setUpdatedAt(Instant.now());
    withdrawalRepository.save(withdrawalRequest);
    return withdrawalRequest;
  }
}