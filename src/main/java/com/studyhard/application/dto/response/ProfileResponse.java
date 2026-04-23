package com.studyhard.application.dto.response;

import com.studyhard.application.model.Gender;
import com.studyhard.application.model.GenderEnum;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileResponse {
  LocalDate birthDate;
  GenderEnum gender;
  String address;
  String fullName;
  String bio;
  String avatar;
  String company;
  Long yearOfExperience = 0L;
  String educationLevel;
  String facebook;
  String personalWebsite;
}
