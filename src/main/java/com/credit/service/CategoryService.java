package com.credit.service;

import com.credit.dto.CategoryDto;
import com.credit.exception.BusinessException;
import com.credit.exception.NotFoundException;
import com.credit.mapper.CategoryMapper;
import com.credit.model.Category;
import com.credit.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Transactional
  public CategoryDto create(CategoryDto dto) {
    Category category = categoryMapper.toEntity(dto);
    return categoryMapper.toDto(categoryRepository.save(category));
  }

  @Transactional(readOnly = true)
  public CategoryDto findById(Long id) {
    return categoryRepository.findById(id)
        .map(categoryMapper::toDto)
        .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id));
  }

  @Transactional(readOnly = true)
  public List<CategoryDto> findAll() {
    return categoryRepository.findAll().stream()
        .map(categoryMapper::toDto)
        .toList();
  }

  @Transactional
  public CategoryDto update(Long id, CategoryDto dto) {
    Category existingCategory = categoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Category not found"));

    existingCategory.setName(dto.getName());
    existingCategory.setRate(dto.getRate());

    return categoryMapper.toDto(categoryRepository.save(existingCategory));
  }

  @Transactional
  public void deleteById(Long id) {
    categoryRepository.deleteById(id);
  }

  public void doubleSaveDemo(List<CategoryDto> dtos) {
    for (CategoryDto dto : dtos) {

      if (dto.getRate() != null && dto.getRate() < 0) {
        throw new BusinessException("Rate cannot be negative!");
      }

      Category category = categoryMapper.toEntity(dto);
      categoryRepository.save(category);
    }
  }
}
