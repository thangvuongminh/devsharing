package com.studyhard.application.utils;

import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class UserExtractor {
  public  static Long getUserId(){
    JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.valueOf(jwtAuthenticationToken.getToken().getClaims().get("userId").toString());
    return userId;
  }
  public  static Boolean checkIsModerator(Long userId){
    JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    @SuppressWarnings("unchecked")
    List<String> allRoles =(List<String>)jwtAuthenticationToken.getToken().getClaims().get("roles");
    if(allRoles.contains("MODERATOR")){
      return true;
    }
    return false;
  }
}
