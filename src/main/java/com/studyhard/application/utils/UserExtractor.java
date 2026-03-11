package com.studyhard.application.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class UserExtractor {
  public  static Long getUserId(){
    JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    Long userId =(Long) jwtAuthenticationToken.getToken().getClaims().get("userId");
    return userId;
  }
}
