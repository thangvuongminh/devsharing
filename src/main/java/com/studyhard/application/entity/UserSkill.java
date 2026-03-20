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

  @Column(name = "skill_name", nullable = false, length = 100)
  String skillName;

  @Column(name = "proficiency_level")
  Integer proficiencyLevel;

  @Column(name = "years_of_experience")
  Integer yearsOfExperience;

  @Column(name = "created_at", updatable = false)
  Instant createdAt;

  @Column(name = "updated_at")
  Instant updatedAt;

  @Column(name = "created_by")
  Long createdBy;

  @Column(name = "updated_by")
  Long updatedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id", nullable = false)
  UserProfile userProfile;
}