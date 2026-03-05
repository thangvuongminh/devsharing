package com.studyhard.application.entity;


import com.studyhard.application.model.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "role")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  @Column(name = "role_name", nullable = false)
  @Enumerated(EnumType.STRING)
  RoleEnum roleName;
  @Column(name = "description", nullable = false)
  String description;
  @OneToMany(mappedBy = "role",fetch = FetchType.LAZY)
  List<UserRole> userRole;
}