package com.studyhard.application.entity;

import com.studyhard.application.model.GenderEnum;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profile")
public class UserProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "birthdate")
  LocalDate birthDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", length = 10)
  GenderEnum gender;

  @Column(name = "address", length = 255)
  String address;

  @Column(name = "bio", columnDefinition = "TEXT")
  String bio;

  @Column(name = "avatar", length = 255)
  String avatar;

  @Column(name = "company", length = 100)
  String company;

  @Column(name = "year_experience")
  @Builder.Default
  Long yearOfExperience = 0L;

  @Column(name = "education_level", length = 100)
  String educationLevel;

  @Column(name = "facebook", length = 255)
  String facebook;

  @Column(name = "personal_website", length = 255)
  String personalWebsite;

  @Column(name = "created_at", updatable = false)
  Instant createdAt;

  @Column(name = "updated_at")
  Instant updatedAt;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
  User user;
}