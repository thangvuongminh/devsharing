package com.studyhard.application.service;

import com.studyhard.application.dto.request.CreateCategoryRequest;
import com.studyhard.application.dto.request.UpdateCategoryRequest;
import com.studyhard.application.entity.Category;
import com.studyhard.application.exception.ExceptionEnum;
import com.studyhard.application.exception.StudyHardException;
import com.studyhard.application.repository.CategoryRepository;
import com.studyhard.application.service.impl.CategoryServiceImpl;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.time.Instant;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {
  @Mock
  CategoryRepository categoryRepository;
  @InjectMocks
  CategoryServiceImpl categoryServiceImpl;
  Category category;
  @BeforeEach
  public void setup() {
    category = Category.builder()
        .name("React JS")
        .description("Các khóa học về lập trình React JS")
        .slug("react")
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }
  @Test
  public void findCategoryByIdTest() {
    given(categoryRepository.findById(any())).willReturn(Optional.of(category));
    Category category1=categoryServiceImpl.getCategoryById(any());
    assertThat( category1).isNotNull();
    assertThat(category1.getName()).isEqualTo(category.getName());
  }
  @Test
  public void findCategoryByIdThrowTest() {
    given(categoryRepository.findById(any())).willReturn(Optional.empty());
    Assertions.assertThrows(StudyHardException.class, () -> categoryServiceImpl.getCategoryById(any()));
  }
  @Test
  public void createCategoryTest() {
    CreateCategoryRequest createCategoryRequest =CreateCategoryRequest.builder()
        .name("React JS")
        .description("Course React js")
        .slug("react")
        .build();
    given(categoryRepository.save(any(Category.class))).willReturn(category);
    categoryServiceImpl.createCategory(createCategoryRequest);
    verify(categoryRepository).save(any(Category.class));
  }

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
}
