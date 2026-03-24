package com.credit.controller;

import com.credit.dto.CategoryDto;
import com.credit.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @PostMapping
  public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto categoryDto) {
    return new ResponseEntity<>(categoryService.create(categoryDto), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<CategoryDto>> getAll() {
    return ResponseEntity.ok(categoryService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(categoryService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDto> update(@PathVariable Long id,
      @RequestBody CategoryDto categoryDto) {
    return ResponseEntity.ok(categoryService.update(id, categoryDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    categoryService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}