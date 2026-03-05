package com.studyhard.application.entity;


import com.studyhard.application.model.UserStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  @Column(name = "username", nullable = false)
  String userName;
  @Column(name = "email", nullable = false, unique = true)
  String email;
  @Column(name = "password", nullable = false)
  String password;
  @Column(name = "firstname", nullable = false)
  String firstName;
  @Column(name = "lastname", nullable = false)
  String lastName;
  @Column(name = "phone_number", nullable = false)
  String phoneNumber;
  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  UserStatus status;
  @Column(name = "created_at", nullable = false)
  Instant createAt;
  @Column(name = "updated_at", nullable = false)
  Instant updateAt;
  @Column(name = "created_by", nullable = false)
  long createBy;
  @Column(name = "updated_by", nullable = false)
  long updateBy;
  @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
  List<UserRole> userRole;
  @OneToOne(cascade = CascadeType.ALL,mappedBy = "user")
  UserProfile userProfile;
  @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
  List<UserVerification> userVerifications;
}