package com.studyhard.application.config;

import com.studyhard.application.entity.UserRole;
import com.studyhard.application.model.RoleEnum;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.repository.UserRoleRepository;
import com.studyhard.application.utils.UserExtractor;
import java.security.Principal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class WebSocketEventListener {
  RedisTemplate<String, Object> redisTemplate;
  UserRoleRepository userRoleRepository;
  @EventListener
  @Transactional
  public void handleModeratorOffline(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    Principal auth = accessor.getUser();
    if (auth == null) return;

    Long userId = Long.valueOf(auth.getName());

    List<UserRole> userRoles = userRoleRepository.findByUserCustomQuery(userId);
    boolean isModerator = userRoles.stream()
        .anyMatch(ur -> RoleEnum.MODERATOR.equals(ur.getRole().getRoleName()));

    if (isModerator) {
      redisTemplate.opsForList().remove("moderator_is_online",1, userId);
    }
  }
}
