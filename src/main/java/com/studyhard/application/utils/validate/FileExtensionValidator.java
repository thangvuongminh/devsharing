package com.studyhard.application.utils.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileExtensionValidator  implements ConstraintValidator<ValidFileExtension, MultipartFile> {
  private String[] allowedTypes;
  @Override
  public void initialize(ValidFileExtension constraintAnnotation) {
    this.allowedTypes = constraintAnnotation.allowedTypes();
  }
  @Override
  public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
    if (value==null) {
      return false;
    }
    String type=value.getContentType();
    for(String allowedType:this.allowedTypes){
      if(allowedType.equalsIgnoreCase(type)){
        return true;
      }
    }
    return false;
  }
}
