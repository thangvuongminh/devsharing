package com.studyhard.application.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

public class TranslationsPrePrompt {
  ChatClient chatClient;

  private static final String DEFAULT_PROMPT_TEMPLATE ="""
			Given a user query, translate it to {targetLanguage}.
			If the query is already in {targetLanguage}, return it unchanged.
			If you don't know the language of the query, return it unchanged.
			Do not add explanations nor any other text.

			Original query: {query}

			Translated query:
			""";
  public   TranslationsPrePrompt(ChatClient.Builder chatClientBuilder) {
    chatClient = chatClientBuilder.build();
  }
  public String translate(String query,String targetLanguage) {
    String userLanguage = DEFAULT_PROMPT_TEMPLATE.replace("{targetLanguage}",targetLanguage)
        .replace("{query}",query);
    return  chatClient.prompt().user(userLanguage).call().content();
  }
}
