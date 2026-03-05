package com.studyhard.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StudyHardException extends   RuntimeException{
  StudyHardExceptionInfo info;
  Object[] args;
  public StudyHardException(StudyHardExceptionInfo info){
    this.info = info;
  }
}
