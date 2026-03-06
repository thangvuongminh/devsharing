package com.studyhard.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class StudyHardApplication {

  public static void main(String[] args) {
    SpringApplication.run(StudyHardApplication.class, args);
  }

}
