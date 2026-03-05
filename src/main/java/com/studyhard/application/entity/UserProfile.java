package com.studyhard.application.entity;
import com.studyhard.application.model.GenderEnum;
import jakarta.persistence.*;
import java.util.Date;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

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
  @Column(name = "birthdate", nullable = false)
  Date birthDate;
  @Column(name = "gender", nullable = false)
  GenderEnum gender;
  @Column(name = "address", nullable = false)
  String address;
  @Column(name = "bio", nullable = false)
  String bio;
  @Column(name = "avatar", nullable = false)
  String avatar;
  @Column(name = "company", nullable = false)
  String company;
  @Column(name = "year_experience", nullable = false)
  Long yearOfExperience;
  @Column(name = "education_level", nullable = false)
  String educationLevel;
  @Column(name = "facebook", nullable = false)
  String facebook;
  @Column(name = "personal_website", nullable = false)
  String personalWebsite;
  @Column(name = "created_at", nullable = false)
  Instant createAt;
  @Column(name = "updated_at", nullable = false)
  Instant updateAt;
  @OneToOne
  @JoinColumn(name = "user_id",referencedColumnName = "id",nullable = false)
  User user;
}