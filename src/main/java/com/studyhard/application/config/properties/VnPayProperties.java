package com.studyhard.application.config.properties;


import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix = "vnpay")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VnPayProperties {
  String vnpVersion;
  String vnpCommand;
  String vnpTmnCode;
  String vnpCurrCode;
  String vnpLocale;
  String vnpOrderType;
  String vnpSecureHash;
  String vnpPayUrl;
  String vnpReturnUrl;
  int expireTime;
}