package com.studyhard.application.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class AiConfig {

  @Value("classpath:ai/contentModerator.st")
  Resource defaultSystem;

  @Bean
  public ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder()
        .maxMessages(10)
        .build();
  }

  @Bean
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder,
      CustomUsageAdvisor customUsageAdvisor, ChatMemory chatMemory,ToolCalling toolCalling) {
    SimpleLoggerAdvisor simpleLoggerAdvisor = SimpleLoggerAdvisor.builder()
        .build();
    MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
        .build();
    ChatOptions chatOptions = ChatOptions.builder()
        .temperature(0.7)
        .topP(0.7)
        .build();
    return chatClientBuilder
        .defaultOptions(chatOptions)
        .defaultSystem(defaultSystem)
        .defaultTools(toolCalling)
            .
        defaultAdvisors(simpleLoggerAdvisor, customUsageAdvisor, messageChatMemoryAdvisor)
        .build();
  }

}
