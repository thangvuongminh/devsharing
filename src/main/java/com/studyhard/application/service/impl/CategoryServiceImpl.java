package com.studyhard.application.service.impl;

import com.studyhard.application.dto.request.CreateCategoryRequest;
import com.studyhard.application.dto.request.UpdateCategoryRequest;
import com.studyhard.application.entity.Category;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.repository.CategoryRepository;
import com.studyhard.application.service.CategoryService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  @Override
  @Transactional
  public Category createCategory(CreateCategoryRequest createCategoryRequest) {
    Category category=Category.builder()
        .name(createCategoryRequest.getName())
        .description(createCategoryRequest.getDescription())
        .slug(createCategoryRequest.getSlug())
        .build();
    categoryRepository.save(category);
    return category;
  }

  @Override
  @Transactional
  public Category updateCategory(Long categoryId, UpdateCategoryRequest updateCategoryRequest) {
    Category category=categoryRepository.findById(categoryId).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.CATEGORY_NOT_FOUND)
    );
    if(updateCategoryRequest.getName()!=null) {
      category.setName(updateCategoryRequest.getName());
    }
    if(updateCategoryRequest.getDescription()!=null) {
      category.setDescription(updateCategoryRequest.getDescription());
    }
    categoryRepository.save(category);
    return category;
  }
  @Override
  @Transactional
  public void  deleteCategory(Long categoryId) {
    Category category=categoryRepository.findById(categoryId).orElseThrow(
        () -> new StudyHardException(ExceptionEnum.CATEGORY_NOT_FOUND)
    );
    categoryRepository.delete(category);
  }

  @Override
  @Transactional
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }
}
