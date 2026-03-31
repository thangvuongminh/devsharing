package com.studyhard.application.service.impl;

import com.studyhard.application.dto.WalletDto;
import com.studyhard.application.dto.request.DepositRequest;
import com.studyhard.application.entity.Transaction;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.Wallet;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.mapper.WalletMapper;
import com.studyhard.application.model.TransactionType;
import com.studyhard.application.repository.TransactionRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.WalletRepository;
import com.studyhard.application.service.WalletService;
import com.studyhard.application.utils.UserExtractor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class WalletServiceImpl implements WalletService {
  WalletRepository walletRepository;
  WalletMapper walletMapper;
  TransactionRepository transactionRepository;
  private static final BigDecimal VND_TO_CREDIT_RATE=new BigDecimal("1000");
  UserRepository userRepository;
  @Transactional
  public Wallet createWallet(Long userId) {
    User user=userRepository.findById(userId).get();
    Wallet wallet = Wallet.builder().user(user)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .balance(BigDecimal.ZERO)
        .build();
    walletRepository.save(wallet);
    return wallet;
  }


  @Override
  @Transactional(readOnly = true)
  public Wallet getWallet(Long userId) {
    return walletRepository.findByUserId(userId).orElseThrow(() -> new StudyHardException(ExceptionEnum.WALLET_NOT_FOUND));
  }


  @Override
  @Transactional
  public Wallet getOrCreateWallet(Long userId) {
    return walletRepository.findByUserId(userId).orElseGet(() ->createWallet(userId));
  }



  @Override
  @Transactional
  public WalletDto deposit(DepositRequest depositRequest) {
    Long userId= UserExtractor.getUserId();
    User user=userRepository.findById(userId).get();
    Wallet wallet = walletRepository.findByUserIdWithLock(user)
        .orElse(getOrCreateWallet(userId));

    BigDecimal creditAmount=depositRequest.getAmountVnd().divide(VND_TO_CREDIT_RATE,2, RoundingMode.HALF_UP);
    BigDecimal balanceBefore=wallet.getBalance();
    BigDecimal balanceAfter=wallet.getBalance().add(creditAmount);
    recordTransaction(userId,TransactionType.DEPOSIT,balanceBefore,creditAmount,balanceAfter,depositRequest.getDescription(),null);
    wallet.setBalance(balanceAfter);
    wallet.setUpdatedAt(Instant.now());
    walletRepository.save(wallet);

    return walletMapper.toWalletDto(wallet);
  }

  @Override
  @Transactional
  public void recordTransaction(Long userId, TransactionType type, BigDecimal amount,
      BigDecimal balanceBefore, BigDecimal balanceAfter, String description, String referenceId) {
    User user=userRepository.findById(userId).get();
    Transaction transaction=Transaction.builder()
        .user(user)
        .amount(amount)
        .balanceBefore(balanceBefore)
        .balanceAfter(balanceAfter)
        .description(description)
        .referenceId(referenceId)
        .type(type)
        .createdAt(Instant.now())
        .build();

    transactionRepository.save(transaction);
  }
  @Override
  @Transactional
  public void deductCredit(User user,BigDecimal amount,TransactionType type,String description,String referenceId) {
    Wallet wallet=walletRepository.findByUserIdWithLock(user).orElseThrow(() -> new StudyHardException(
        ExceptionEnum.WALLET_NOT_FOUND));
    BigDecimal balanceBefore=wallet.getBalance();
    BigDecimal balanceAfter=wallet.getBalance().subtract(amount);
    wallet.setBalance(balanceAfter);
    wallet.setUpdatedAt(Instant.now());
    walletRepository.save(wallet);
    Transaction transaction=Transaction.builder()
        .user(user)
        .type(type)
        .amount(amount.negate())
        .balanceBefore(balanceBefore)
        .balanceAfter(balanceAfter)
        .createdAt(Instant.now())
        .referenceId(referenceId)
        .description(description)
        .build();
    transactionRepository.save(transaction);

  }

  @Override
  public void addCredit(Long userId, BigDecimal amount, TransactionType type, String description,
      String referenceId) {
    User user1=userRepository.findById(userId).get();
    Wallet wallet=walletRepository.findByUserIdWithLock(user1).orElseThrow(() -> new StudyHardException(
        ExceptionEnum.WALLET_NOT_FOUND));
    BigDecimal balanceBefore=wallet.getBalance();
    BigDecimal balanceAfter=wallet.getBalance().add(amount);
    wallet.setBalance(balanceAfter);
    wallet.setUpdatedAt(Instant.now());
    walletRepository.save(wallet);
    User user=userRepository.findById(userId).get();
    Transaction transaction=Transaction.builder()
        .user(user)
        .type(type)
        .amount(amount.negate())
        .balanceBefore(balanceBefore)
        .balanceAfter(balanceAfter)
        .createdAt(Instant.now())
        .referenceId(referenceId)
        .description(description)
        .build();
    transactionRepository.save(transaction);
  }
}
