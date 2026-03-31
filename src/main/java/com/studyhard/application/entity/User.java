package com.studyhard.application.entity;

import com.studyhard.application.model.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "username", nullable = false, unique = true, length = 50)
  String username;

  @Column(name = "email", nullable = false, unique = true, length = 150)
  String email;

  @Column(name = "password", nullable = false, length = 255)
  String password;

  @Column(name = "full_name", length = 100)
  String fullName;

  @Column(name = "phone_number", length = 20)
  String phoneNumber;

  @Column(name = "status", nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  UserStatus status;

  @Column(name = "created_at", updatable = false)
  Instant createdAt;

  @Column(name = "updated_at")
  Instant updatedAt;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  List<UserRole> userRole;



  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  List<UserVerification> userVerifications;

  @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
  UserProfile userProfile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  Wallet wallet;
}