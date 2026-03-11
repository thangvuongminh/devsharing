package com.studyhard.application.dto.request;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DepositRequest {
  @NotNull(message = "{deposit.amountVnd.notnull}")
  @DecimalMin(value = "1000",message = "{deposit.amountVnd.min}")
  BigDecimal amountVnd;
  String description;
}