package com.studyhard.application.service.impl;

import com.studyhard.application.service.NotificationService;
import com.studyhard.application.utils.RandomOtp;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  JavaMailSender mailSender;
  @NonFinal
  @Value("${spring.mail.username}")
  String sender;
  @Override
  @Async
  public void sendEmail(String subject,String msgBody,String email) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(sender);
      message.setTo(email);
      message.setSubject(subject);
      message.setText(msgBody);
      mailSender.send(message);

    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
