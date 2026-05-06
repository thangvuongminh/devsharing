package com.studyhard.application.service.impl;

import com.studyhard.application.dto.AddCreditRequest;
import com.studyhard.application.dto.CreditBalanceDto;
import com.studyhard.application.dto.CreditTransactionDto;
import com.studyhard.application.dto.request.DeductCreditRequest;
import com.studyhard.application.dto.request.OrderRequest;
import com.studyhard.application.entity.Transaction;
import com.studyhard.application.entity.User;
import com.studyhard.application.entity.Wallet;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.model.TransactionType;
import com.studyhard.application.repository.TransactionRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.WalletRepository;
import com.studyhard.application.service.CreditApiService;
import com.studyhard.application.service.PaymentService;
import com.studyhard.application.service.WalletService;
import com.studyhard.application.utils.RandomOtp;
import com.studyhard.application.utils.UserExtractor;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreditApiServiceImpl implements CreditApiService {
  PaymentService paymentService;
  WalletRepository walletRepository;
  WalletService walletService;
  UserRepository userRepository;
  RandomOtp randomOtp;
  TransactionRepository transactionRepository;
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
    walletService.addCredit(request.getUserId(), amount,
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

  @Override
  public String createOrder(OrderRequest orderRequest, HttpServletRequest request) {
    Long userId= UserExtractor.getUserId();
    User user=userRepository.findById(userId).get();
    String contentOrder="Deposit"+ UUID.randomUUID().toString();
    Wallet wallet=walletService.getOrCreateWallet(user.getId());
    BigDecimal amount = BigDecimal.valueOf(orderRequest.getTotal()/1000);
    BigDecimal blanceBefore=wallet.getBalance();
    BigDecimal blanceAfter=wallet.getBalance().add(amount);
    String  txnRef=null;
    Optional<Transaction> transactionExist;
    for (int i=0;i<100;++i){
      txnRef=String.valueOf(randomOtp.generateTxnRef());
      transactionExist=transactionRepository.findByReferenceId(txnRef);
      if(transactionExist.isEmpty()){
        break;
      }
    }
    Transaction transaction= Transaction.builder()
        .user(user)
        .type(TransactionType.PENDING_DEPOSIT)
        .balanceBefore(blanceBefore)
        .balanceAfter(blanceAfter)
        .description(contentOrder)
        .amount(amount)
        .referenceId(String.valueOf(txnRef))
        .createdAt(Instant.now())
        .build();
    transactionRepository.save(transaction);
    return paymentService.createOrder(orderRequest.getTotal(),contentOrder,UserExtractor.getIpAddress(request),txnRef);
  }

  @Override
  @Transactional
  public void processIpn(Map<String, String> params) {
      String vnp_TxnRef= paymentService.processIpn(params);
    Transaction transaction = transactionRepository.findByReferenceId(vnp_TxnRef).get();
    transaction.setType(TransactionType.DEPOSIT);
    transactionRepository.save(transaction);
    User user=userRepository.findById(transaction.getUser().getId()).get();
    walletService.addCredit(user.getId(), transaction.getAmount(),
        "Deposit", "");
  }

  @Override
  public Boolean verifyPayment(Map<String, String> params) {
    return paymentService.verifySecureHash(params);
  }

}