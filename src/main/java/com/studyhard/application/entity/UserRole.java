package com.studyhard.application.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_role")
@Builder
public class UserRole {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id",nullable = false)
  User user;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id",nullable = false)
  Role role;
  @Column(name = "created_at", nullable = false)
  Instant createAt;
  @Column(name = "updated_at", nullable = false)
  Instant updateAt;
}