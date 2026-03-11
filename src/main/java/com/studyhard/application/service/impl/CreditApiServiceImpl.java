package com.studyhard.application.service.impl;

import com.studyhard.application.dto.AddCreditRequest;
import com.studyhard.application.dto.CreditBalanceDto;
import com.studyhard.application.dto.CreditTransactionDto;
import com.studyhard.application.dto.request.DeductCreditRequest;
import com.studyhard.application.entity.Wallet;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.model.TransactionType;
import com.studyhard.application.repository.WalletRepository;
import com.studyhard.application.service.CreditApiService;
import com.studyhard.application.service.WalletService;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreditApiServiceImpl implements CreditApiService {

  WalletRepository walletRepository;
  WalletService walletService;

  @Override
  @Transactional(readOnly = true)
  public CreditBalanceDto getBalance(Long userId) {
    Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new StudyHardException(
        ExceptionEnum.WALLET_NOT_FOUND));

    return CreditBalanceDto.builder()
        .userId(wallet.getUserId())
        .balance(wallet.getBalance())
        .build();
  }

  @Override
  @Transactional
  public CreditTransactionDto deductCredit(DeductCreditRequest request) {
    Wallet walletBefore = walletRepository.findByUserIdWithLock(request.getUserId())
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.WALLET_NOT_FOUND));
    walletService.deductCredit(request.getUserId(), request.getAmount(), TransactionType.PURCHASE,
        request.getReason(), request.getReferenceId());
    Wallet walletAfter = walletRepository.findByUserIdWithLock(request.getUserId())
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.WALLET_NOT_FOUND));
    ;
    return CreditTransactionDto.builder()
        .userId(walletAfter.getUserId())
        .referenceId(request.getReferenceId())
        .amount(request.getAmount())
        .balanceBefore(walletBefore.getBalance())
        .balanceAfter(walletAfter.getBalance())
        .description(request.getReason())
        .createdAt(Instant.now())
        .type(TransactionType.PURCHASE.name())
        .build();
  }

  @Override
  public CreditTransactionDto addCredit(AddCreditRequest request) {
    Wallet walletBefore = walletRepository.findByUserIdWithLock(request.getUserId())
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.WALLET_NOT_FOUND));
    walletService.addCredit(request.getUserId(), request.getAmount(), TransactionType.EARNING,
        request.getReason(), request.getReferenceId());
    Wallet walletAfter = walletRepository.findByUserIdWithLock(request.getUserId())
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.WALLET_NOT_FOUND));
    ;
    return CreditTransactionDto.builder()
        .userId(walletAfter.getUserId())
        .referenceId(request.getReferenceId())
        .amount(request.getAmount())
        .balanceBefore(walletBefore.getBalance())
        .balanceAfter(walletAfter.getBalance())
        .description(request.getReason())
        .createdAt(Instant.now())
        .type(TransactionType.EARNING.name())
        .build();
  }
}