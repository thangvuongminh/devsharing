package com.studyhard.application.config;

import com.cloudinary.Cloudinary;
import com.studyhard.application.config.properties.CloudinaryProperties;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
@RequiredArgsConstructor
public class CloudinaryConfig {
  CloudinaryProperties cloudinaryProperties;
  @Bean
  public Cloudinary cloudinary() {
    Map<String,String> config = new HashMap<>();
    config.put("cloud_name",cloudinaryProperties.getCloud_name());
    config.put("api_key",cloudinaryProperties.getApi_key());
    config.put("api_secret",cloudinaryProperties.getApi_secret());
    return new Cloudinary(config);
  }
}
