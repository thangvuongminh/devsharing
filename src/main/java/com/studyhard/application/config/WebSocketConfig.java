package com.studyhard.application.config;

import com.studyhard.application.entity.User;
import com.studyhard.application.entity.UserRole;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.model.RoleEnum;
import com.studyhard.application.mongo.entity.SupportTicket;
import com.studyhard.application.mongo.repository.SupportTicketRepository;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.UserRoleRepository;
import com.studyhard.application.utils.UserExtractor;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  JwtDecoder jwtDecoder;
  UserRepository userRepository;
  SupportTicketRepository supportTicketRepository;
  RedisTemplate<String, Object> redisTemplate;
  UserRoleRepository userRoleRepository;
  Pattern pattern = Pattern.compile("^/[a-z]+/ticket/(.+)");

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("http://localhost:5173")
        .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker( "/queue");
    registry.setApplicationDestinationPrefixes("/app");
    registry.setPreservePublishOrder(true);
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
            StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
          String token = accessor.getFirstNativeHeader("Authorization");

          if (token == null || !token.startsWith("Bearer ")) {
            throw new StudyHardException(ExceptionEnum.INVALID_TOKEN);
          }
          String bearerToken = token.substring(7);
          try {
            Map<String, Object> claims = jwtDecoder.decode(bearerToken).getClaims();
            Long userId = Long.valueOf(claims.get("userId").toString());
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND));
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");
            Jwt jwt = jwtDecoder.decode(bearerToken);
            List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new).toList();
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt,
                authorities);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                authorities
            );
            handleModeratorOnline(userId);
            accessor.setUser(auth);
          } catch (Exception e) {
            log.info("Invalid Bearer Token: {}", e.getMessage());

          }
        }
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
          String path = accessor.getDestination();
          Matcher matcher = pattern.matcher(path);
          if (matcher.find()) {
            String ticketId = matcher.group(1);
            SupportTicket supportTicket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new StudyHardException(ExceptionEnum.SUPPORT_TICKET_NOT_FOND));
            if (!Objects.equals(supportTicket.getUserId(), UserExtractor.getUserId())) {
              throw new StudyHardException(ExceptionEnum.SUPPORT_TICKET_NOT_FOND);
            }
          }
        }
        return message;
      }
    });
  }

  @Transactional
  void handleModeratorOnline(Long userId) {
    List<UserRole> userRoles = userRoleRepository.findByUserId(userId);

    boolean isModerator = userRoles.stream()
        .anyMatch(ur -> RoleEnum.MODERATOR.equals(ur.getRole().getRoleName()));


    if (isModerator) {
      redisTemplate.opsForList().remove("moderator_is_online", 0, userId);
      redisTemplate.opsForList().rightPush("moderator_is_online", userId);
    }
  }
}
