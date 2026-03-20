package com.studyhard.application.service;


import com.studyhard.application.dto.WalletDto;
import com.studyhard.application.dto.request.DepositRequest;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.Wallet;
import com.studyhard.application.model.TransactionType;
import java.math.BigDecimal;

public interface WalletService {

    public Wallet createWallet(Long userId);
  public Wallet getWallet(Long userId);
  public WalletDto deposit(DepositRequest depositRequest);

  public Wallet getOrCreateWallet(Long userId);

  public void recordTransaction(Long userId, TransactionType type, BigDecimal amount,
      BigDecimal balanceBefore, BigDecimal balanceAfter, String description, String referenceId);

  public void deductCredit(User user, BigDecimal amount, TransactionType type, String description,
      String referenceId);

  public void addCredit(Long userId, BigDecimal amount, TransactionType type, String description,
      String referenceId);
}