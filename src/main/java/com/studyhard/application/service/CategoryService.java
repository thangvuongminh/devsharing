package com.studyhard.application.service;

import com.studyhard.application.dto.request.CreateCategoryRequest;
import com.studyhard.application.dto.request.UpdateCategoryRequest;
import com.studyhard.application.entity.Category;
import java.util.List;

public interface CategoryService {

  // Crud only admin
  public Category getCategoryById(Long id);

  public Category createCategory(CreateCategoryRequest createCategoryRequest);

  public Category updateCategory(Long categoryId,
      UpdateCategoryRequest updateCategoryRequest);

  public void deleteCategory(Long categoryId);

  public List<Category> getAllCategories();
}
