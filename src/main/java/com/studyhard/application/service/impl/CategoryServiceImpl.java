package com.studyhard.application.service.impl;

import com.studyhard.application.entity.Category;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.repository.CategoryRepository;
import com.studyhard.application.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class CategoryServiceImpl implements CategoryService {
  CategoryRepository categoryRepository;

  @Override
  public Category getCategoryById(Long id) {
    return categoryRepository.findById(id).orElseThrow(() ->new StudyHardException(
        ExceptionEnum.CATEGORY_NOT_FOUND));
  }
}
