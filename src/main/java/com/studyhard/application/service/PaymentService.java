package com.studyhard.application.service;

import com.studyhard.application.dto.request.OrderRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public abstract class PaymentService {
  public abstract String createOrder(int total, String orderInfo,String ip_address,String txn_code);
  public abstract String processIpn(Map<String, String> params);
  public  abstract  Boolean verifySecureHash(Map<String, String> params);
  public final   String generateHmacSha512(String key, String data) {
    try {
      String algorithm = "HmacSHA512";
      Mac mac = Mac.getInstance(algorithm);
      SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
      mac.init(secretKeySpec);
      byte[] hashBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        hexString.append(String.format("%02x", b));
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException("Lỗi khi tạo mã HMAC-SHA512", e);
    }
  }

}
