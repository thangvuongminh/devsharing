package com.studyhard.application.security;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomFilter implements Filter {

  RedisTemplate<String, Object> redisTemplate;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      String token = jwtAuthenticationToken.getToken().getId();
      String bearerToken = "blacklist " + token;
      String result = (String) redisTemplate.opsForValue().get(bearerToken);
      if (result == null) {
        filterChain.doFilter(request, response);
      } else {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println("Invalid token");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      }
    }else {
      filterChain.doFilter(request, response);
    }
  }
}
