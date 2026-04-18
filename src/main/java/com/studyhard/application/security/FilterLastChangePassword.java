package com.studyhard.application.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilterLastChangePassword implements Filter {

  RedisTemplate<String, Object> redisTemplate;
  @Override
  @SuppressWarnings("unchecked")
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      Instant issuedNoConvert =(Instant) jwtAuthenticationToken.getToken().getClaims().get("iat");
      Long issueAt = issuedNoConvert.getEpochSecond();
      Long userId = Long.valueOf(
          jwtAuthenticationToken.getToken().getClaims().get("userId").toString());
      Long theLastChange=(Long) redisTemplate.opsForHash().get("theLastChangePassword",userId.toString());
      if(theLastChange==null) {
        filterChain.doFilter(request,response);
        return;
      }
      if(theLastChange< issueAt) {
        filterChain.doFilter(request,response);
        return;
      }else {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println("{\"message\": \"Invalid token\"}");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      }
    }else {
      filterChain.doFilter(request,response);
    }
  }
}
