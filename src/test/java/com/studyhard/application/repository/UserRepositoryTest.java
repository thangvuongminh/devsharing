package com.studyhard.application.repository;

import com.studyhard.application.entity.User;
import com.studyhard.application.model.UserStatus;
import java.time.Instant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTest {
  @Autowired
  private UserRepository userRepository;
  @Test
  void createUser() {
    User user =User.builder()
        .userName("thangVM")
        .password("123456")
        .status(UserStatus.ACTIVE)
        .email("chaudiensdk5@gmail.com")
        .build();
    userRepository.save(user);
    Assertions.assertThat(user.getPassword()).isEqualTo("123456");
  }
}
