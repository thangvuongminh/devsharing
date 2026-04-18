package com.studyhard.application.service.impl;

import com.studyhard.application.dto.AddCreditRequest;
import com.studyhard.application.dto.CreditBalanceDto;
import com.studyhard.application.dto.CreditTransactionDto;
import com.studyhard.application.dto.request.DeductCreditRequest;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.Wallet;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.model.TransactionType;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.WalletRepository;
import com.studyhard.application.service.CreditApiService;
import com.studyhard.application.service.WalletService;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
  UserRepository userRepository;
  @Override
  @Transactional(readOnly = true)
  public CreditBalanceDto getBalance(Long userId) {
    Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new StudyHardException(
        ExceptionEnum.WALLET_NOT_FOUND));

    return CreditBalanceDto.builder()
        .userId(wallet.getUser().getId())
        .balance(wallet.getBalance())
        .build();
  }

  @Override
  @Transactional
  public CreditTransactionDto deductCredit(DeductCreditRequest request) {
    User user=userRepository.findById(request.getUserId()).get();
    Wallet walletBefore = walletRepository.findByUserIdWithLock(user)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.WALLET_NOT_FOUND));
    User userBefore = walletBefore.getUser();
    walletService.deductCredit(userBefore, request.getAmount(), TransactionType.PURCHASE,
        request.getReason(), request.getReferenceId());
    Wallet walletAfter = walletRepository.findByUserIdWithLock(user)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.WALLET_NOT_FOUND));
    ;
    return CreditTransactionDto.builder()
        .userId(walletAfter.getUser().getId())
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
    User user=userRepository.findById(request.getUserId()).get();
    Wallet walletBefore = walletRepository.findByUserIdWithLock(user)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.WALLET_NOT_FOUND));
    BigDecimal amount=new BigDecimal(String.valueOf(request.getAmount()));
    amount=amount.divide(WalletServiceImpl.VND_TO_CREDIT_RATE,2, RoundingMode.HALF_UP);
    walletService.addCredit(request.getUserId(), amount, TransactionType.EARNING,
        request.getReason(), request.getReferenceId());
    Wallet walletAfter = walletRepository.findByUserIdWithLock(user)
        .orElseThrow(() -> new StudyHardException(ExceptionEnum.WALLET_NOT_FOUND));
    ;
    return CreditTransactionDto.builder()
        .userId(walletAfter.getUser().getId())
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