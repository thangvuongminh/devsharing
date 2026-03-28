package com.studyhard.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyhard.application.config.properties.SwaggerAccountAccess;
import com.studyhard.application.dto.request.UserLoginRequest;
import com.studyhard.application.dto.response.UserLoginResponse;
import com.studyhard.application.security.SecurityConfig;
import com.studyhard.application.service.UserAccountService;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(UserAccountController.class
)
public class UserAccountServiceTest {
  @MockitoBean
  UserAccountService userAccountService;
  @MockitoBean
  RedisTemplate<String, Object> redisTemplate;
  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @Test
  public void givenValidUser_whenLogin_thenSuccess() throws Exception {
    UserLoginRequest userLoginRequest = UserLoginRequest.builder()
        .username("admin")
        .password("123456")
        .build();
    UserLoginResponse userLoginResponse =UserLoginResponse.builder()
        .userId(1l)
        .accessToken("token")
        .refreshToken("refreshToken")
        .build();
    given(userAccountService.loginUser(any(UserLoginRequest.class))).willReturn(userLoginResponse);
    ResultActions response= mockMvc.perform(post("/user/account/login")

            .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userLoginRequest))
    );
    // then
    response.andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.userId").value(1))
        .andExpect(jsonPath("$.data.accessToken").value("token"))
        .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"))
    ;

  }
}
