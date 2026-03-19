package com.studyhard.application.config.properties;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "swagger.credentials.basic-auth")
@Component
public class SwaggerAccountAccess {
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  static class Account {
    String email;
    String password;
  }
  List<Account> accounts;
  Map<String,String> accountaccess;
  @PostConstruct
  public void init(){
    accountaccess=new HashMap<>();
    accounts.forEach((account)->{
      accountaccess.put(account.getEmail(),account.getPassword());
    });
  }
}
