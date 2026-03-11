package com.studyhard.application.entity;


import com.studyhard.application.model.WithdrawalStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "withdrawal_request")
public class WithdrawalRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "user_id", nullable = false)
  Long userId;

  @Column(name = "amount", nullable = false, precision = 19, scale = 2)
  BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  WithdrawalStatus status;

  @Column(name = "bank_account_number", nullable = false)
  String bankAccountNumber;

  @Column(name = "bank_name", nullable = false)
  String bankName;

  @Column(name = "account_holder_name", nullable = false)
  String accountHolderName;

  @Column(name = "note")
  String note;

  @Column(name = "admin_note")
  String adminNote;

  @Column(name = "reviewed_by")
  Long reviewedBy;

  @Column(name = "reviewed_at")
  Instant reviewedAt;

  @Column(name = "created_at", nullable = false)
  Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  Instant updatedAt;
}