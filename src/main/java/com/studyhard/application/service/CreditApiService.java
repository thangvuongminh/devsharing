package com.studyhard.application.service;


import com.studyhard.application.dto.AddCreditRequest;
import com.studyhard.application.dto.CreditBalanceDto;
import com.studyhard.application.dto.CreditTransactionDto;
import com.studyhard.application.dto.request.DeductCreditRequest;

public interface CreditApiService {
  CreditBalanceDto getBalance(Long userId);
  CreditTransactionDto deductCredit(DeductCreditRequest request);
  CreditTransactionDto addCredit(AddCreditRequest request);

}