package com.studyhard.application.dto;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreditTransactionDto {

  Long id;
  Long userId;
  String type;
  BigDecimal amount;
  BigDecimal balanceBefore;
  BigDecimal balanceAfter;
  String description;
  String referenceId;
  Instant createdAt;
}