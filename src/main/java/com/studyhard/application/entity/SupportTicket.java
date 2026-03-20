package com.studyhard.application.entity;


import com.studyhard.application.model.SupportTicketStatus;

import jakarta.persistence.*;

import java.time.Instant;

import java.util.List;

import lombok.*;

import lombok.experimental.FieldDefaults;


@Getter

@Setter

@Builder

@NoArgsConstructor

@AllArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)

@Table(name = "support_ticket")

@Entity

public class SupportTicket {


  @Id

  @GeneratedValue(strategy = GenerationType.IDENTITY)

  Long id;


  @ManyToOne(fetch = FetchType.LAZY)

  @JoinColumn(name = "userId")

  User user;


  @Column(name = "issue", columnDefinition = "TEXT", nullable = false)

  String issue;


  @Enumerated(EnumType.STRING)

  @Column(name = "status", length = 50)

  SupportTicketStatus status;


  @Column(name = "create_at", updatable = false)

  Instant createAt;


  @OneToMany(mappedBy = "supportTicket", cascade = CascadeType.ALL)

  List<TicketMessage> ticketMessages;

}