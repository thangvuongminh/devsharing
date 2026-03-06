package com.studyhard.application.controller;

import com.studyhard.application.utils.RandomOtp;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("HI")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HelloController {
  RandomOtp randomOtp;
  @GetMapping
  public String hello(){
    String otp=String.valueOf(randomOtp.generateOtp());
    return otp;
  }
}
