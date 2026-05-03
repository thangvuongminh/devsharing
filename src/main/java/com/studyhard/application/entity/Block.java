package com.studyhard.application.entity;

import com.studyhard.application.model.BlockType;
import jakarta.persistence.*;
import java.util.LinkedList;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "block")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Block {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  BlockType type;
  String title;
  @Column(name = "text_content", columnDefinition = "TEXT")
  String textContent;

  @Column(name = "properties", columnDefinition = "JSON")
  String properties;

  @Column(name = "position")
  @Builder.Default
  Integer position = 0;

  @Column(name = "is_free")
  @Builder.Default
  Boolean isFree = false;

  @Column(name = "created_at", updatable = false)
  Instant createdAt;

  @Column(name = "updated_at")
  Instant updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "content_id", nullable = false)
  Content content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_block_id")
  Block parentBlock;

  @OneToMany(mappedBy = "parentBlock", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("position ASC")
  @Builder.Default
  List<Block> children = new ArrayList<>();
}