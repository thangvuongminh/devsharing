package com.studyhard.application.dto.response;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class VnPayResponse {
  String vnp_TmnCode;
  long vnp_Amount;
  String vnp_BankCode;
  String vnp_BankTranNo;
  String vnp_CardType;
  String vnp_PayDate;
  String vnp_OrderInfo;
  String vnp_TransactionNo;
  String vnp_ResponseCode;
  String vnp_TransactionStatus;
  String vnp_TxnRef;
  String vnp_SecureHash;
}
