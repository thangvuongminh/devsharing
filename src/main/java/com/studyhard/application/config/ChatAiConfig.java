package com.studyhard.application.config;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatAiConfig {
  @Bean
  public ChatMemory  chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(jdbcChatMemoryRepository)
        .maxMessages(10)
        .build();
  }
  @Bean
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder,CustomAdvisorUsed customAdvisorUsed,ChatMemory chatMemory) {

    ChatOptions chatOption= ChatOptions.builder()
        .temperature(0.8)
        .build();
    Advisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
    return chatClientBuilder
//        .defaultSystem("""
//            As an HR professional conducting a Java Backend interview, please provide insightful and constructive responses. Kindly disregard any unrelated questions.
//            """)
        .defaultAdvisors(List.of(new SimpleLoggerAdvisor(),customAdvisorUsed,chatMemoryAdvisor))
        .defaultOptions(chatOption)
        .build();
  }
}
