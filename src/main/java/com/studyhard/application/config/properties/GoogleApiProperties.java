package com.studyhard.application.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api.google")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoogleApiProperties {
  String clientId;
  String clientSecret;
  String redirectUri;
  String token;
  String accessToken;
  String grantType;
}
