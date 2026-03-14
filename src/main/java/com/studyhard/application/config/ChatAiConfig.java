package com.studyhard.application.config;

import java.util.List;
import java.util.Vector;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatAiConfig {

  VectorStore vectorStore;
  @NonFinal
  @Value("classpath:ai/supportCustomerTicket.st")
  Resource supportCustomerTicket;
  @Bean
  public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(jdbcChatMemoryRepository)
        .maxMessages(10)
        .build();
  }
  @Bean
  public RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(ChatClient.Builder chatClientBuilder) {
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
  public  ToolExecutionExceptionProcessor  toolExecutionExceptionProcessor() {
    return new DefaultToolExecutionExceptionProcessor(true);
  }
  @Bean("helpChatClientSupport")
  public ChatClient chatClientSupport(ChatClient.Builder chatClientBuilder,
      CustomAdvisorUsed customAdvisorUsed, ChatMemory chatMemory,ToolCallingConfigForSupportTicket toolCallingConfigForSupportTicket) {
    ChatOptions chatOption = ChatOptions.builder()
        .temperature(0.8)
        .build();
    Advisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
    return chatClientBuilder
        .defaultTools(toolCallingConfigForSupportTicket)
        .defaultSystem(supportCustomerTicket  )
        .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), customAdvisorUsed,chatMemoryAdvisor))
        .defaultOptions(chatOption)
        .build();
  }
  @Bean("supportCustomer")
  public ChatClient chatClient(ChatClient.Builder chatClientBuilder,
      CustomAdvisorUsed customAdvisorUsed, ChatMemory chatMemory,RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
    ChatOptions chatOption = ChatOptions.builder()
        .temperature(0.8)
        .build();
    Advisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
    return chatClientBuilder
        .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), customAdvisorUsed,retrievalAugmentationAdvisor))
        .defaultOptions(chatOption)
        .build();
  }

}
