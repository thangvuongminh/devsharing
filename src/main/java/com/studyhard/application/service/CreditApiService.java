package com.studyhard.application.service;


import com.studyhard.application.dto.AddCreditRequest;
import com.studyhard.application.dto.CreditBalanceDto;
import com.studyhard.application.dto.CreditTransactionDto;
import com.studyhard.application.dto.request.DeductCreditRequest;
import com.studyhard.application.dto.request.OrderRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface CreditApiService {
  CreditBalanceDto getBalance(Long userId);
  CreditTransactionDto deductCredit(DeductCreditRequest request);
  CreditTransactionDto addCredit(AddCreditRequest request);
  String createOrder(OrderRequest orderRequest, HttpServletRequest request);
  void processIpn(Map<String, String> params);
  Boolean verifyPayment(Map<String, String> params);
}