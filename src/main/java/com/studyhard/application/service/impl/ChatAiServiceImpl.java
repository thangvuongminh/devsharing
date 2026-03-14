package com.studyhard.application.service.impl;

import com.studyhard.application.entity.User;
import com.studyhard.application.service.ChatAiService;
import com.studyhard.application.utils.UserExtractor;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.print.Doc;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ChatAiServiceImpl implements ChatAiService {
  ChatAiServiceImpl(@Qualifier("helpChatClientSupport")  ChatClient chatClient,  VectorStore vectorStore){
    this.chatClient = chatClient;
    this.vectorStore = vectorStore;
  }
  VectorStore vectorStore;
  ChatClient chatClient;
  @Value("classpath:ai/supportCustomer.st")
  Resource supportCustomer;
  @Override
  public String supportCustomer(String message) {
    return chatClient.prompt()
        .toolContext(Map.of("userId", UserExtractor.getUserId()))
        .advisors(advisorSpec ->
            advisorSpec.param(ChatMemory.CONVERSATION_ID, UserExtractor.getUserId()))
            .
        user(message)
        .call().content();
  }
}
