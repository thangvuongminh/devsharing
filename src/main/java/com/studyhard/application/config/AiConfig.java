package com.studyhard.application.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.mongo.MongoChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiConfig {

  MongoChatMemoryRepository memoryRepository;
  @NonFinal
  @Value("classpath:ai/contentModerator.st")
  Resource defaultSystem;

  @Bean
  ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
    return new DefaultToolExecutionExceptionProcessor(true);
  }

  @Bean
  public ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(memoryRepository)
        .maxMessages(10)
        .build();
  }

  @Bean(name = "evaluationUserMessages")
  public ChatClient chatClientAdvisor(Builder chatClientBuilder,
      CustomUsageAdvisor customUsageAdvisor) {
    SimpleLoggerAdvisor simpleLoggerAdvisor = SimpleLoggerAdvisor.builder()
        .build();
    ChatOptions chatOptions = ChatOptions.builder()
        .temperature(0.7)
        .topP(0.7)
        .build();
    return chatClientBuilder
        .defaultOptions(chatOptions)
            .
        defaultAdvisors(simpleLoggerAdvisor, customUsageAdvisor)
        .build();
  }
  @Bean(name = "checkEligibilityCreator")
  public  ChatClient chatClientForCreator(ChatClient.Builder chatClientBuilder,CustomUsageAdvisor customUsageAdvisor,ChatMemory chatMemory) {
    SimpleLoggerAdvisor simpleLoggerAdvisor = new SimpleLoggerAdvisor();
    MessageChatMemoryAdvisor messageChatMemoryAdvisor= MessageChatMemoryAdvisor.builder(chatMemory).build() ;
    return chatClientBuilder
        .defaultAdvisors(simpleLoggerAdvisor,customUsageAdvisor)
        .build();
  }

  @Bean
  @Primary
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder,
      CustomUsageAdvisor customUsageAdvisor, ChatMemory chatMemory) {
    SimpleLoggerAdvisor simpleLoggerAdvisor = SimpleLoggerAdvisor.builder()
        .build();
    MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(
            chatMemory)
        .build();
    ChatOptions chatOptions = ChatOptions.builder()
        .temperature(0.7)
        .topP(0.7)
        .build();
    return chatClientBuilder
        .defaultOptions(chatOptions)
        .defaultSystem(defaultSystem)
            .
        defaultAdvisors(simpleLoggerAdvisor, customUsageAdvisor, messageChatMemoryAdvisor)
        .build();
  }

}
