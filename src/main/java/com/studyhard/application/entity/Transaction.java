package com.studyhard.application.entity;

import com.studyhard.application.model.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "`transaction`")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 50)
  TransactionType type;

  @Column(name = "amount", precision = 19, scale = 2, nullable = false)
  BigDecimal amount;

  @Column(name = "balance_before", precision = 19, scale = 2, nullable = false)
  BigDecimal balanceBefore;

  @Column(name = "balance_after", precision = 19, scale = 2, nullable = false)
  BigDecimal balanceAfter;

  @Column(name = "description", columnDefinition = "TEXT")
  String description;

  @Column(name = "reference_id", length = 255)
  String referenceId;

  @Column(name = "created_at", nullable = false, updatable = false)
  Instant createdAt;
}