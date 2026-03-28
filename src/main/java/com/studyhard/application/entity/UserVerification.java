package com.studyhard.application.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "user_verification")
public class UserVerification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "verification_code", nullable = false, length = 255)
  String verificationCode;

  @Column(name = "expired_at", nullable = false)
  Instant expiredAt;

  @Column(name = "channel", nullable = false, length = 50)
  String channel;

  @Column(name = "verified_at")
  Instant verifiedAt;

  @Column(name = "receiver", nullable = false, length = 255)
  String receiver;

  @Column(name = "created_at", updatable = false)
  Instant createdAt;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  User user;
}