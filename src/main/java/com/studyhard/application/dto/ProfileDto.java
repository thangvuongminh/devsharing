package com.studyhard.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.studyhard.application.model.GenderEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class ProfileDto {
  LocalDate birthDate;
  GenderEnum gender;
  String address;
  String bio;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  String avatar;
  String company;
  @Min(1)
  @Max(50)
  Long yearOfExperience = 0L;
  String educationLevel;
  String facebook;
  String personalWebsite;
}
