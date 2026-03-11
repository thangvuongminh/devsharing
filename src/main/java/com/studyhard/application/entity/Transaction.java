package com.studyhard.application.entity;
import com.studyhard.application.model.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  @Column(name = "user_id",updatable = true,nullable = false)
  Long userId;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  TransactionType type;
  @Column(precision = 19,scale = 2,nullable = false)
  BigDecimal amount;
  @Column(precision = 19,scale = 2,nullable = false,name = "balance_before")
  BigDecimal balanceBefore;
  @Column(precision = 19,scale = 2,nullable = false,name = "balance_after")
  BigDecimal balanceAfter;
  String description;
  @Column(name = "reference_id")
  String referenceId;
  @Column(name = "created_at",nullable = false)
  Instant createAt;
}