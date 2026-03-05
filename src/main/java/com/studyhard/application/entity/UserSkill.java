package com.studyhard.application.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_skill")
public class UserSkill {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  @Column(name = "skill_name", nullable = false)
  String skillName;
  @Column(name = "proficiency_level", nullable = false)
  Byte proficiencyLevel;
  @Column(name = "yearOfExperience", nullable = false)
  Byte yearOfExperience;
  @Column(name = "created_at", nullable = false)
  Instant createAt;
  @Column(name = "updated_at", nullable = false)
  Instant updateAt;
  @Column(name = "created_by", nullable = false)
  long createBy;
  @Column(name = "updated_by", nullable = false)
  long updateBy;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id")
  UserProfile userProfile;
}