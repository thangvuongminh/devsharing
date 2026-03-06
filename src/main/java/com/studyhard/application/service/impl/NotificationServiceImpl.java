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
  RandomOtp randomOtp;
  RedisTemplate<String,Object> redisTemplate;
  @NonFinal
  @Value("${spring.mail.username}")
  String sender;
  @Override
  @Async
  public void sendEmail(String email) {
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(sender);
      message.setTo(email);
      message.setSubject("StudyHard Yêu cầu khôi phục mật khẩu");
      String otpCode=String.valueOf(randomOtp.generateOtp());
      String msgBody = """
    Chào bạn,
    
    Bạn vừa gửi yêu cầu khôi phục mật khẩu. Vui lòng sử dụng mã xác minh dưới đây:
    
    Mã OTP của bạn là:  %s
    
    (Mã này có hiệu lực trong 5 phút . Tuyệt đối không chia sẻ mã này cho bất kỳ ai).
    
    Nếu không phải bạn thực hiện yêu cầu này, hãy đổi mật khẩu ngay lập tức hoặc liên hệ hỗ trợ.
    """.formatted(otpCode);
      message.setText(msgBody);
      mailSender.send(message);
      redisTemplate.opsForValue().set(otpCode,"forgot-password", Duration.ofSeconds(300));
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}
