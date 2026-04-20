package com.studyhard.application.utils.validate;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
    validatedBy = {FileExtensionValidator.class}
)
public @interface ValidFileExtension {
  String message() default "File not valid";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
  String[] allowedTypes() default {"image/jpeg", "image/png", "application/pdf"};
}
