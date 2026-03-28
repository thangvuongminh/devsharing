package com.studyhard.application.intergration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mysql.MySQLContainer;

public class MySqlConfigContainer {
  private static  final  MySQLContainer mysql;
  static{
    mysql=new MySQLContainer("mysql:8.0.36").withUsername("root").withPassword("root")
        .withDatabaseName("studyhard_test");
    mysql.start();
  }
  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry){
    registry.add("spring.datasource.url", mysql::getJdbcUrl);
    registry.add("spring.datasource.username", mysql::getUsername);
    registry.add("spring.datasource.password", mysql::getPassword);
    registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
  }
}
