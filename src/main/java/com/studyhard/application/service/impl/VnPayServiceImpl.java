package com.studyhard.application.service.impl;

import com.studyhard.application.config.properties.VnPayProperties;
import com.studyhard.application.dto.request.OrderRequest;
import com.studyhard.application.dto.response.VnPayResponse;
import com.studyhard.application.entity.Transaction;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.model.TransactionType;
import com.studyhard.application.repository.TransactionRepository;
import com.studyhard.application.service.PaymentService;
import com.studyhard.application.utils.RandomOtp;
import com.studyhard.application.utils.UserExtractor;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VnPayServiceImpl extends PaymentService {

  VnPayProperties vnPayProperties;
  RandomOtp randomOtp;
  TransactionRepository transactionRepository;
  @Override
  public String createOrder(int total, String orderInfo, String ip_address,String txn_code) {
    Map<String, String> vnpayParams = new HashMap<>();
    vnpayParams.put("vnp_Version", vnPayProperties.getVnpVersion());
    vnpayParams.put("vnp_Command", vnPayProperties.getVnpCommand());
    vnpayParams.put("vnp_TmnCode", vnPayProperties.getVnpTmnCode());
    vnpayParams.put("vnp_Amount", String.valueOf(total * 100));
    vnpayParams.put("vnp_CurrCode", vnPayProperties.getVnpCurrCode());
    vnpayParams.put("vnp_TxnRef", txn_code);
    vnpayParams.put("vnp_OrderInfo", orderInfo);
    vnpayParams.put("vnp_OrderType", "order-type");
    vnpayParams.put("vnp_ReturnUrl", vnPayProperties.getVnpReturnUrl());
    vnpayParams.put("vnp_IpAddr", ip_address);
    vnpayParams.put("vnp_Locale", vnPayProperties.getVnpLocale());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expireTime = now.plus(vnPayProperties.getExpireTime(), ChronoUnit.SECONDS);
    vnpayParams.put("vnp_CreateDate", now.format(formatter));
    vnpayParams.put("vnp_ExpireDate", expireTime.format(formatter));
    List<String> fieldNames = new ArrayList<>(vnpayParams.keySet()).stream()
        .filter(k -> vnpayParams.get(k) != null && !vnpayParams.get(k).isEmpty()
        ).sorted().toList();
    String hashData = fieldNames.stream()
        .map(k -> k + "=" + URLEncoder.encode(vnpayParams.get(k), StandardCharsets.UTF_8).replace("+", "%20"))
        .collect(Collectors.joining("&"));
    String query = fieldNames.stream()
        .map(k -> {
          String encodedKey = URLEncoder.encode(k, StandardCharsets.UTF_8).replace("+", "%20");
          String encodedValue = URLEncoder.encode(vnpayParams.get(k), StandardCharsets.UTF_8).replace("+", "%20");
          return encodedKey + "=" + encodedValue;
        })
        .collect(Collectors.joining("&"));
    String vnp_SecureHash = generateHmacSha512(vnPayProperties.getVnpSecureHash(), hashData);
    query += "&vnp_SecureHash=" + vnp_SecureHash;
    return vnPayProperties.getVnpPayUrl() + "?" + query;
  }

  @Override
  public String  processIpn(Map<String, String> params) {
    if (verifySecureHash(params)) {
      if (params.containsKey("vnp_TransactionStatus")) {
        if (params.get("vnp_TransactionStatus").equals("00")) {
           return params.get("vnp_TxnRef");

        }
      }
    }
    throw  new StudyHardException(ExceptionEnum.DEPOSIT_ERROR);
  }
  public  Boolean verifySecureHash(Map<String, String> params){
    String secureHash = params.get("vnp_SecureHash");
    params.remove("vnp_SecureHash");
    List<String> fieldNames = new ArrayList<>(params.keySet()).stream()
        .filter(k -> params.get(k) != null && !params.get(k).isEmpty()
        ).sorted().toList();
    String hashData = fieldNames.stream()
        .map(k -> k + "=" + URLEncoder.encode(params.get(k), StandardCharsets.UTF_8).replace("+", "%20"))
        .collect(Collectors.joining("&"));
    String vnp_SecureHash = generateHmacSha512(vnPayProperties.getVnpSecureHash(), hashData);
    return secureHash.equals(vnp_SecureHash);
  }
}
