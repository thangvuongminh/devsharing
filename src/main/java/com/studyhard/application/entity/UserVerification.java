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
  @Column(name = "verification_code", nullable = false)
  String verificationCode;
  @Column(name = "expired_at", nullable = false)
  Instant expiredAt;
  @Column(name = "channel", nullable = false)
  String channel;
  @Column(name = "verified_at", nullable = false)
  Instant verifiedAt;
  @Column(name = "receiver", nullable = false)
  String receiver;
  @Column(name = "created_at", nullable = false)
  Instant createAt;
  @Column(name = "updated_at", nullable = false)
  Instant updateAt;
  @Column(name = "created_by", nullable = false)
  long createBy;
  @Column(name = "updated_by", nullable = false)
  long updateBy;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  User user;
}