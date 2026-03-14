package com.studyhard.application.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  @Column(length = 100,nullable = false)
  String name;
  @Length(max =500)
  String description;
  @Column(length = 50)
  String slug;
  @Timestamp
  @Column(name = "created_at")
  Instant createdAt;
  @Timestamp
  @Column(name = "updated_at")
  Instant updatedAtt;
}