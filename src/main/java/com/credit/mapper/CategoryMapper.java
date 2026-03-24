package com.credit.mapper;

import com.credit.dto.CategoryDto;
import com.credit.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

  public CategoryDto toDto(Category category) {
    if (category == null) {
      return null;
    }
    return CategoryDto.builder()
        .id(category.getId())
        .name(category.getName())
        .rate(category.getRate())
        .build();
  }

  public Category toEntity(CategoryDto dto) {
    if (dto == null) {
      return null;
    }
    return Category.builder()
        .id(dto.getId())
        .name(dto.getName())
        .rate(dto.getRate())
        .build();
  }
}