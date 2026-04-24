package com.studyhard.application.service.impl;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

import com.studyhard.application.dto.request.RegisterCreatorRequest;
import com.studyhard.application.dto.response.RegisterCreatorResponse;
import com.studyhard.application.service.AiService;
import com.studyhard.application.utils.UserExtractor;
import java.io.IOException;
import java.util.Map;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService {

  ChatClient chatClient;

  public AiServiceImpl(@Qualifier("checkEligibilityCreator") ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  @Override
  public RegisterCreatorResponse verifyCreator(RegisterCreatorRequest registerCreatorRequest) throws IOException {
    Long userId = UserExtractor.getUserId();
    String type = registerCreatorRequest.getEvident().getContentType();
    MediaType mediaType = MediaType.parseMediaType(type);
    return chatClient.prompt()
        .toolContext(Map.of("userId",userId))
        .user(u -> u.text(registerCreatorRequest.getDesc())
            .media(mediaType, registerCreatorRequest.getEvident().getResource())).call()
        .entity(RegisterCreatorResponse.class);
  }
}
