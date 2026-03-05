package com.studyhard.application.config;

import com.studyhard.application.entity.User;
import com.studyhard.application.repository.UserRepository;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class StudyHardUserDetailService implements UserDetailsService {
  UserRepository userRepository;
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user=userRepository.findByUserName(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));

    return new UserDetails() {
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
      }

      @Override
      public String getPassword() {
        return  user.getPassword();
      }

      @Override
      public String getUsername() {
        return user.getUserName();
      }
    } ;
  }
}
