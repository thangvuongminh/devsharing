package com.studyhard.application.entity;

import com.studyhard.application.model.WithdrawalStatus;
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
@Table(name = "withdrawal_request")
public class WithdrawalRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  User user;

  @Column(name = "amount", nullable = false, precision = 19, scale = 2)
  BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  WithdrawalStatus status;

  @Column(name = "bank_account_number", nullable = false, length = 50)
  String bankAccountNumber;

  @Column(name = "bank_name", nullable = false, length = 100)
  String bankName;

  @Column(name = "account_holder_name", nullable = false, length = 100)
  String accountHolderName;

  @Column(name = "note", columnDefinition = "TEXT")
  String note;

  @Column(name = "admin_note", columnDefinition = "TEXT")
  String adminNote;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewed_by")
  User reviewer;

  @Column(name = "reviewed_at")
  Instant reviewedAt;

  @Column(name = "created_at", updatable = false)
  Instant createdAt;

  @Column(name = "updated_at")
  Instant updatedAt;
}