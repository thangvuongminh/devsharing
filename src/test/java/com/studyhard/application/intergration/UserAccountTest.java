package com.studyhard.application.intergration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyhard.application.dto.request.UserLoginRequest;
import com.studyhard.application.dto.request.UserRegisterRequest;
import com.studyhard.application.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import  org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAccountTest {
  @Autowired
  MockMvc mockMvc;
  @Autowired
  UserRepository userRepository;
//  @BeforeEach
//  void setup() {
//    userRepository.deleteAll();
//  }
  @Autowired
  ObjectMapper objectMapper;
  @Test
  @Transactional
  @Order(1)
  void registerUserTest() throws Exception {
    userRepository.deleteAll();
    UserRegisterRequest userRegisterRequest= UserRegisterRequest.builder()
        .username("thangDZz")
        .password("admin")
        .email("n23dccn124@student.ptithcm.edu.vn")
        .build();
    ResultActions response= mockMvc.perform(post("/user/account/register")
        .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRegisterRequest))

    );
    response.andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.username").value("thangDZz"))
        .andExpect(jsonPath("$.data.email").value("n23dccn124@student.ptithcm.edu.vn"));
  }
//  @Test
//  @Order(2)
//  void loginUserTest() throws Exception {
//    UserLoginRequest userLoginRequest= UserLoginRequest.builder()
//        .username("thangDZ")
//        .password("admin")
//        .build();
//    ResultActions response= mockMvc.perform(post("/user/account/login")
//        .contentType(MediaType.APPLICATION_JSON)
//        .content(objectMapper.writeValueAsString(userLoginRequest))
//
//    );
//    response.andDo(MockMvcResultHandlers.print())
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.data.accessToken").value(String.class))
//        .andExpect(jsonPath("$.data.refreshToken").value(String.class));
//  }
}
