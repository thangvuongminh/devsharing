package com.studyhard.application.mapper;

import com.studyhard.application.dto.CategoryDto;
import com.studyhard.application.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE,
unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
  CategoryDto toCategoryDto(Category category);
}
