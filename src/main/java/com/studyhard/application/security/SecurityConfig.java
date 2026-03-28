package com.studyhard.application.security;

import com.studyhard.application.config.StudyHardUserDetailService;
import com.studyhard.application.config.properties.SwaggerAccountAccess;
import com.studyhard.application.service.NotificationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.Md4PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

  RedisTemplate<String, Object> redisTemplate;
  SwaggerAccountAccess swaggerAccountAccess;
  @Bean
  @Order(3)
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
      JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            (authorize) -> authorize.requestMatchers("/v3/api-docs/**", "/swagger-ui/**")
                .permitAll()
                // user account
                .requestMatchers("/user/account/**").permitAll()
                .requestMatchers("/chat/**").permitAll()
                .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(
            jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)))
        .addFilterAfter(new CustomFilter(redisTemplate), BearerTokenAuthenticationFilter.class)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
    ;
    ;
    return http.build();
  }

//  @Bean
//  @Order(2)
//  public SecurityFilterChain securityFilterChainGoogle(HttpSecurity http
//  ) throws Exception {
//    http.csrf(AbstractHttpConfigurer::disable)
//        .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()));
//            http.securityMatcher("/user/account/google","/v3/api-docs/**", "/swagger-ui/**").authorizeHttpRequests(
//                (authorize) -> authorize.
//                    requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll().
//                    requestMatchers(
//                    request -> {
//                      String bearerToken = request.getHeader("Authorization");
//                      return bearerToken != null && bearerToken.startsWith("Bearer ");
//                    }
//                ).permitAll().anyRequest().authenticated()
//            );
//    return http.build();
//  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Profile("!local")
  public SecurityFilterChain securityFilterChainSwagger(HttpSecurity http
  ) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()));
    http.securityMatcher(  "v3/api-docs/**")
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults()).
        authenticationManager(
        authenticationProvider -> {
          String email= authenticationProvider.getPrincipal().toString();
          String password=authenticationProvider.getCredentials().toString();
          Map<String,String> accountAccess =swaggerAccountAccess.getAccountaccess();
          if(accountAccess.containsKey(email)){
            String passwordSystem=accountAccess.get(email);
            if(passwordSystem.equals(password)){
              return new  UsernamePasswordAuthenticationToken(email,password,
                  List.of(new SimpleGrantedAuthority("ADMIN")));
            }
          }
          throw new UsernameNotFoundException(email);
        }
    );
    return http.build();
  }


  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.addAllowedOrigin("*");
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.addAllowedMethod("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    String encodingId = "bcrypt";
    Map<String, PasswordEncoder> encoders = new HashMap();
    encoders.put("bcrypt", new BCryptPasswordEncoder());
    encoders.put("MD5", new Md4PasswordEncoder());
    DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(encodingId,
        encoders);
    delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService userDetailsService)
      throws Exception {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(
        userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return new ProviderManager(authenticationProvider);
  }
}
