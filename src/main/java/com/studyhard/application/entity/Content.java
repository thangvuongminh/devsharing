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
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Content {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  @Column(length = 255, nullable = false)
  String title;
  @Column(columnDefinition = "MEDIUMTEXT")
  String description;
  @Column(nullable = false,name = "creator_id")
  Long creatorId;
  @Column(name = "category_id")
  Long categoryId;
  @Enumerated(EnumType.STRING)
  ContentStatus status;
  @Enumerated(EnumType.STRING)
  ContentLevel level;
  @Column(precision = 19, scale = 2)
  BigDecimal price;
  @Column(name = "view_count")
  Long viewCount=0L;
  @Column(name = "purchase_count")
  Long purchaseCount=0L;
  @Column(name = "published_at")
  Instant publishedAt;
  @Column(name = "created_at")
  Instant createdAt;
  @Column(name = "updated_at")
  Instant updatedAt;

  @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Block> blocks = new ArrayList<>();
}