package com.studyhard.application.service;

import com.studyhard.application.entity.Category;

public interface CategoryService {
  // Crud only admin
  public Category getCategoryById(Long id);
}
