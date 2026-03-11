package com.studyhard.application.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Component;

@Slf4j
@Component("CustomAdvisor")
public class CustomAdvisorUsed implements CallAdvisor {

  @Override
  public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest,
      CallAdvisorChain callAdvisorChain) {
    ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
    assert chatClientResponse.chatResponse() != null;
    Usage usage = chatClientResponse.chatResponse().getMetadata().getUsage();
    log.info("Usage " + usage.toString());
    return chatClientResponse;
  }

  @Override
  public String getName() {
    return "CustomAdvisorUsed";
  }

  @Override
  public int getOrder() {
    return 1;
  }
}
