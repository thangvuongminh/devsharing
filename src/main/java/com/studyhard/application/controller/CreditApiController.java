package com.studyhard.application.controller;

import com.cloudinary.Api;
import com.studyhard.application.dto.AddCreditRequest;
import com.studyhard.application.dto.CreditBalanceDto;
import com.studyhard.application.dto.CreditTransactionDto;
import com.studyhard.application.dto.request.DeductCreditRequest;
import com.studyhard.application.dto.request.OrderRequest;
import com.studyhard.application.entity.Transaction;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.repository.TransactionRepository;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.CreditApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Credit Api", description = "API credits for internal service")
public class CreditApiController {

  TransactionRepository transactionRepository;
  CreditApiService creditApiService;

  @GetMapping("/wallets/balance/{userId}")
  @PreAuthorize("isAuthenticated()")
  @Operation(
      summary = "Get user balance",
      description = "Get credit balance by user ID (for internal service calls)"
  )
  public ResponseEntity<ApiResponse<CreditBalanceDto>> getBalance(@PathVariable String userId) {
    CreditBalanceDto balance = creditApiService.getBalance(Long.valueOf(userId));
    return ResponseEntity.ok(ApiResponse.success(balance));
  }

  @PostMapping("/transactions/deduct")
  @PreAuthorize("isAuthenticated()")
  @Operation(
      summary = "Deduct credit",
      description = "Deduct credit from user wallet (called by purchase-service)"
  )
  public ResponseEntity<ApiResponse<CreditTransactionDto>> deduct(
      @Valid @RequestBody DeductCreditRequest request) {
    CreditTransactionDto transaction = creditApiService.deductCredit(request);
    return ResponseEntity.ok(ApiResponse.success(transaction));
  }

  @PostMapping("/transactions/deposit")
  public ResponseEntity<ApiResponse<?>> createInvoiceDeposit(
      @RequestBody OrderRequest orderRequest, HttpServletRequest request) {
    String createOrder = creditApiService.createOrder(orderRequest, request);
    return ResponseEntity.ok(ApiResponse.success(createOrder));
  }

  @PostMapping("/transactions/add")
  @PreAuthorize("isAuthenticated()")
  @Operation(
      summary = "Add credit",
      description = "Add credit to user wallet (called by purchase-service for creator commission)"
  )
  public ResponseEntity<ApiResponse<CreditTransactionDto>> add(
      @Valid @RequestBody AddCreditRequest request) {
    CreditTransactionDto transaction = creditApiService.addCredit(request);
    return ResponseEntity.ok(ApiResponse.success(transaction));
  }

  @GetMapping("vnpay/ipn")
  public ResponseEntity<ApiResponse<Void>> processIpn(
      @RequestParam Map<String, String> params) {
    creditApiService.processIpn(params);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/payment-confirm")
  public ResponseEntity<ApiResponse<?>> confirmPayment(@RequestParam Map<String, String> params) {
    if (creditApiService.verifyPayment(params)) {
      if (params.get("vnp_TxnRef").equals("00")) {
        Transaction transaction = transactionRepository.findByReferenceId(params.get("vnp_TxnRef")).orElseThrow(() -> new StudyHardException(
            ExceptionEnum.TRANSACTION_NOT_FOUND));
      }

    } ;
  }
}