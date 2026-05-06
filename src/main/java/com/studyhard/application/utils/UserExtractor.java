package com.studyhard.application.utils;

import jakarta.servlet.http.HttpServletRequest;
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
  public static String getIpAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-Forwarded-For");

    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_CLIENT_IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
    }

    // Nếu có nhiều IP (do qua nhiều proxy), lấy cái đầu tiên
    if (ipAddress != null && ipAddress.contains(",")) {
      return ipAddress.split(",")[0].trim();
    }

    return ipAddress;
  }
}
