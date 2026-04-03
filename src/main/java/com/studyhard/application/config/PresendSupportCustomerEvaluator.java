package com.studyhard.application.config;

import com.studyhard.application.mongo.entity.ChatMessage;
import com.studyhard.application.mongo.repository.SupportTicketRepository;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.Evaluator;
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PresendSupportCustomerEvaluator implements Evaluator {
  ChatClient chatClient;
  String contextHistory;
  public PresendSupportCustomerEvaluator(ChatClient chatClient,String contextHistory) {
    this.chatClient = chatClient;
    this.contextHistory = contextHistory;
  }
  private static final String DEFAULT_EVALUATION_PROMPT_TEXT = """
      Your task is to analyze the provided chat history and determine if the customer has agreed to meet or talk with a Customer Service Representative (CSR).
      
      ### RULES:
      1. Respond with "YES" only if the customer explicitly or implicitly agrees, confirms, or shows willingness to have a meeting, a phone call, or a direct interaction with the Customer Service team.
      2. Respond with "NO" if the customer declines, says they do not need further help, or if the chat history does not contain a clear agreement.
      3. Constraint: You MUST output ONLY the word "YES" or the word "NO". Do not provide any explanations, notes, or additional text.
      
      ### CHAT HISTORY:
      {context}
      
      ### CURRENT USER INPUT:
      {user_query}
      
      ### ANSWER:
      """;

  @Override
  public EvaluationResponse evaluate(EvaluationRequest evaluationRequest) {
    String userMessage = DEFAULT_EVALUATION_PROMPT_TEXT.replace("{context}",contextHistory)
        .replace("{user_query}",evaluationRequest.getUserText());
        ;
    String evaluationResponse=chatClient.prompt()
        .user(userMessage).call().content();
    boolean passing = false;
    if("yes".equalsIgnoreCase(evaluationResponse)) {
      passing = true;
    }
    return new EvaluationResponse(passing, -1, "", Collections.emptyMap());
  }
}
