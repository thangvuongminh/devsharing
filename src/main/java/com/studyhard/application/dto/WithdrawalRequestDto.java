package com.studyhard.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WithdrawalRequestDto {
  Long id;
  Long userId;
  @NotNull(message = "{withdrawal.amount.positive}")
  @DecimalMin(value = "10", message = "{withdrawal.amount.min}")
  BigDecimal amount;
  @NotBlank(message = "{withdrawal.bankAccountNumber.notBlank}")
  String bankAccountNumber;
  @NotBlank(message = "{withdrawal.bankName.notBlank}")
  String bankName;
  @NotBlank(message = "{withdrawal.accountHolderName.notBlank}")
  String accountHolderName;

  String note;
  String adminNote;
  Long reviewedBy;
  Instant reviewedAt;
  Instant createdAt;
  Instant updatedAt;
}