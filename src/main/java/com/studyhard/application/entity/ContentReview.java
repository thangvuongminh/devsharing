package com.studyhard.application.entity;

import com.studyhard.application.model.ReviewAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

  @Column(name = "moderator_id", nullable = false)
  private Long moderatorId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  ReviewAction action;

  @Column(columnDefinition = "TEXT")
  private String feedback;

  @Column(name = "reviewed_at", nullable = false)
  private Instant reviewedAt;
}