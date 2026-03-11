package com.studyhard.application.controller;

import com.studyhard.application.dto.WithdrawalRequestDto;
import com.studyhard.application.dto.request.ReviewWithdrawalRequest;
import com.studyhard.application.entity.WithdrawalRequest;
import com.studyhard.application.mapper.WithdrawalMapper;
import com.studyhard.application.response.ApiResponse;
import com.studyhard.application.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("withdrawal")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WithdrawalController {
  WithdrawalService withdrawalService;
  WithdrawalMapper withdrawalMapper;
  @PostMapping
  @PreAuthorize("hasAnyRole('CREATOR','CONSUMER')")
  public ResponseEntity<ApiResponse<WithdrawalRequestDto>>  createWithdrawalRequest(@RequestBody WithdrawalRequestDto withdrawalRequestDto){
    WithdrawalRequest withdrawalRequest=withdrawalService.createWithdrawalRequest(withdrawalRequestDto);
    WithdrawalRequestDto response=withdrawalMapper.toWithdrawalRequestDto(withdrawalRequest);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
  @GetMapping("/my-requests")
  @PreAuthorize("hasAnyRole('CREATOR','CONSUMER')")
  public ResponseEntity<ApiResponse<Page<WithdrawalRequestDto>>> getMyWithdrawalRequests(
      Pageable pageable){
    Page<WithdrawalRequest> withdrawalRequests=withdrawalService.getUserWithdrawalRequests(pageable);
    Page<WithdrawalRequestDto> withdrawalRequestDtos=withdrawalRequests.map(withdrawalMapper::toWithdrawalRequestDto);
    return ResponseEntity.ok(ApiResponse.success(withdrawalRequestDtos));
  }
  @GetMapping("/{requestId}")
  @PreAuthorize("hasAnyRole('CREATOR','ADMIN')")
  public ResponseEntity<ApiResponse<WithdrawalRequestDto>> getWithdrawalRequest(@PathVariable String requestId){
    Long requestIdLong=Long.parseLong(requestId);
    WithdrawalRequest withdrawalRequest=withdrawalService.getWithdrawalRequest(requestIdLong);
    WithdrawalRequestDto response=withdrawalMapper.toWithdrawalRequestDto(withdrawalRequest);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
  @GetMapping("/admin/pendings")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Page<WithdrawalRequestDto>>> getPendingWithdrawalRequests(Pageable pageable){
    Page<WithdrawalRequest> withdrawalRequests=withdrawalService.getPendingWithdrawalRequests(pageable);
    Page<WithdrawalRequestDto> withdrawalRequestDtos=withdrawalRequests.map(withdrawalMapper::toWithdrawalRequestDto);
    return ResponseEntity.ok(ApiResponse.success(withdrawalRequestDtos));
  }
  @PutMapping("/admin/{requestId}/review")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<WithdrawalRequestDto>> reviewWithdrawalRequest(
      @PathVariable Long requestId,
      @Valid @RequestBody ReviewWithdrawalRequest review
  ) {
    WithdrawalRequest result = withdrawalService.reviewWithdrawalRequest(requestId, review);
    WithdrawalRequestDto response=withdrawalMapper.toWithdrawalRequestDto(result);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

}