package com.studyhard.application.controller;

import com.studyhard.application.dto.WalletDto;
import com.studyhard.application.dto.request.DepositRequest;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.WalletService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("wallet")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletController {
  WalletService walletService;
  @PostMapping("/deposit")
  @PreAuthorize("hasAnyRole('CONSUMER','CREATOR')")
  public ResponseEntity<ApiResponse<WalletDto>> deposit(@Valid @RequestBody DepositRequest depositRequest){
    WalletDto walletDto = walletService.deposit(depositRequest);
    return  ResponseEntity.ok().body(ApiResponse.success(walletDto));
  }
}
