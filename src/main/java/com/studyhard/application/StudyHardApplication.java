package com.studyhard.application;

import com.studyhard.application.config.properties.VnPayProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableMongoRepositories(basePackages = "com.studyhard.application.mongo.repository")
public class StudyHardApplication    {
  public static void main(String[] args) {
    SpringApplication.run(StudyHardApplication.class, args);
  }
}
