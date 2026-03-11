package com.studyhard.application.config;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatAiConfig {
  @Bean
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder,CustomAdvisorUsed customAdvisorUsed) {
    ChatOptions chatOption= ChatOptions.builder()
        .temperature(0.8)
        .build();
    return chatClientBuilder
        .defaultSystem("""
            As an HR professional conducting a Java Backend interview, please provide insightful and constructive responses. Kindly disregard any unrelated questions.
            """)
        .defaultAdvisors(List.of(new SimpleLoggerAdvisor(),customAdvisorUsed))
        .defaultOptions(chatOption)
        .build();
  }
}
