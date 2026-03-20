package com.studyhard.application.entity;

import com.studyhard.application.model.ContentLevel;
import com.studyhard.application.model.ContentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "content")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Content {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "title", length = 255, nullable = false)
  String title;

  @Column(name = "description", columnDefinition = "TEXT")
  String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id", nullable = false)
  User creator;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  Category category;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  ContentStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "level", length = 20)
  ContentLevel level;

  @Column(name = "price", precision = 19, scale = 2)
  BigDecimal price;

  @Column(name = "view_count")
  @Builder.Default
  Long viewCount = 0L;

  @Column(name = "purchase_count")
  @Builder.Default
  Long purchaseCount = 0L;

  @Column(name = "published_at")
  Instant publishedAt;

  @Column(name = "created_at", updatable = false)
  Instant createdAt;

  @Column(name = "updated_at")
  Instant updatedAt;

  @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  List<Block> blocks = new ArrayList<>();
}