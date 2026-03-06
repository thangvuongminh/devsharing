package com.studyhard.application.utils;

import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class RandomOtp {
  public Integer generateOtp() {
    Random random = new Random();
    Integer otp=    random.nextInt(9);
    for (int i=0; i<5; i++) {
      otp*=10;
      otp+=random.nextInt(10);
    }
    return otp;
  }
}
