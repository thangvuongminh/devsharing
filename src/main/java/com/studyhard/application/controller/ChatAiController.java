package com.studyhard.application.controller;

import com.studyhard.application.entity.User;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.repository.UserRepository;
import com.studyhard.application.utils.UserExtractor;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.expression.ExpressionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatAiController {

  ChatClient chatClient;
  UserRepository userRepository;
  @NonFinal
  @Value("classpath:/promptTemplate/defaultTemplateUser.st")
  Resource defaultTemplateUserResource;
  @GetMapping
  public String chat(@RequestParam("message") String message) {
    User user = userRepository.findById(UserExtractor.getUserId()).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.USERNAME_NOT_FOUND)
    );
    String name = user.getEmail();
    return chatClient.prompt()
        .user(u -> u.text(defaultTemplateUserResource).param("message", message))
        .call().content();
  }
}
