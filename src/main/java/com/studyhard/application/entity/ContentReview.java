package com.studyhard.application.entity;

import com.studyhard.application.model.ReviewAction;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "content_review")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentReview {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "content_id", nullable = false)
  Content content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "moderator_id", nullable = false)
  User moderator;

  @Enumerated(EnumType.STRING)
  @Column(name = "action", length = 50)
  ReviewAction action;

  @Column(name = "feedback", columnDefinition = "TEXT")
  String feedback;

  @Column(name = "action_at", nullable = false, updatable = false)
  Instant actionAt;
}