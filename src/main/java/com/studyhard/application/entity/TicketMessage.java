package com.studyhard.application.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "ticket_message")
@Entity
public class TicketMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(name = "message", columnDefinition = "TEXT", nullable = false)
  String message;

  @Column(name = "create_at", updatable = false)
  Instant createAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId")
  User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "support_ticket")
  SupportTicket supportTicket;
}