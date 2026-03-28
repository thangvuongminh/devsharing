package com.studyhard.application.repository;

import com.studyhard.application.entity.User;
import com.studyhard.application.model.UserStatus;
import java.time.Instant;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
@DataJpaTest(
    properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false"  // thêm dòng này
    }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
  @Autowired
  private UserRepository userRepository;
  @Test
  void createUser() {
    User user =User.builder()
        .username("thangVM")
        .password("123456")
        .status(UserStatus.ACTIVE)
        .email("chaudiensdk5@gmail.com")
        .build();
    userRepository.save(user);
    Assertions.assertThat(user.getPassword()).isEqualTo("123456");
  }
  @Test
  void getAllUsers() {
    User user =User.builder()
        .username("thangVM")
        .password("123456")
        .status(UserStatus.ACTIVE)
        .email("chaudiengsdk5@gmail.com")
        .build();
    User user1 =User.builder()
        .username("thangVMr")
        .password("123456")
        .status(UserStatus.ACTIVE)
        .email("chiensdk5@gmail.com")
        .build();
    User user2 =User.builder()
        .username("thangVMrO")
        .password("123456")
        .status(UserStatus.ACTIVE)
        .email("chaud@gmail.com")
        .build();
    userRepository.save(user);
    userRepository.save(user1);
    userRepository.save(user2);
    List<User> users = userRepository.findAll();
    Assertions.assertThat(users.size()).isGreaterThanOrEqualTo(3);
    Assertions.assertThat(users.get(0).getUsername()).isEqualTo("thangVM");
    Assertions.assertThat(users).matches(l -> l.get(0).getEmail().equals("chaudiensdk5@gmail.com"));
  }
}
