package com.studyhard.application.utils;

import com.studyhard.application.config.properties.JwtProperties;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenerateToken {
  JwtProperties jwtProperties;
  JwtEncoder jwtEncoder;
  MacAlgorithm macAlgorithm=MacAlgorithm.HS256;
  public   String createToken(Long userId,TokenType type, String... roles){
    Boolean access_token=type.equals(TokenType.ACCESS_TOKEN);
    JwsHeader jwsHeader=JwsHeader.with(macAlgorithm).build();
    JwtClaimsSet jwtClaimsSet= JwtClaimsSet.builder()
        .expiresAt(Instant.now().plusSeconds(access_token?jwtProperties.getTimeLiveAccessToken():jwtProperties.getTimeLiveRefreshToken()))
        .subject(access_token? "access_token":"refresh_token")
        .issuedAt(Instant.now())
        .id(UUID.randomUUID().toString())
        .claim("userId", userId)
        .claim("roles",roles)
        .build();
    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader,jwtClaimsSet)).getTokenValue();
  }
}
