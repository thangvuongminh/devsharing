package com.studyhard.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "message_and_images_become_creator")
public class BecomeCreatorMessagesImages {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  String message;
  @ManyToOne
  @JoinColumn(name = "become_creator")
  BecomeCreator becomeCreator;
  @Column(name = "thumb_url")
  String thumbUrl;
}
