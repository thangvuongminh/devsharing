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
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.mongo.MongoChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
  public ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(memoryRepository)
        .maxMessages(10)
        .build();
  }

  @Bean
  public RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(
      Builder chatClientBuilder,VectorStore vectorStore) {
    return RetrievalAugmentationAdvisor.builder()
        .queryTransformers(TranslationQueryTransformer.builder()
            .chatClientBuilder(chatClientBuilder.clone())
            .targetLanguage("english")
            .build())
        .documentRetriever(
            VectorStoreDocumentRetriever.builder()
                .similarityThreshold(0.6)
                .vectorStore(vectorStore)
                .build()
        )
        .build();
  }
    @Bean
    public ChatClient chatClient (Builder chatClientBuilder,
        CustomUsageAdvisor customUsageAdvisor, ChatMemory chatMemory, ToolCalling toolCalling){
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
          .defaultTools(toolCalling)
              .
          defaultAdvisors(simpleLoggerAdvisor, customUsageAdvisor, messageChatMemoryAdvisor)
          .build();
    }

  }
