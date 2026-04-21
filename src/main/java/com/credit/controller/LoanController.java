package com.credit.controller;

import com.credit.dto.LoanDto;
import com.credit.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

  private final LoanService loanService;

  @PostMapping
  public ResponseEntity<LoanDto> create(@RequestBody LoanDto loanDto) {
    return new ResponseEntity<>(loanService.create(loanDto), HttpStatus.CREATED);
  }

  /**
   * Получить все кредиты с пагинацией (кэшируется).
   */
  @GetMapping
  public ResponseEntity<Page<LoanDto>> getAll(
      @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
    return ResponseEntity.ok(loanService.findAll(pageable));
  }

  @GetMapping("/profile/{profileId}")
  public ResponseEntity<Page<LoanDto>> getByProfile(
      @PathVariable Long profileId,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByProfileId(profileId, pageable));
  }

  @GetMapping("/filter")
  public ResponseEntity<Page<LoanDto>> getByState(
      @RequestParam String state,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByState(state, pageable));
  }

  // ==================== JPQL эндпоинты (кэшируются) ====================

  @GetMapping("/jpql/by-category")
  public ResponseEntity<Page<LoanDto>> getByCategoryName(
      @RequestParam String name,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryName(name, pageable));
  }

  @GetMapping("/jpql/by-lastname")
  public ResponseEntity<Page<LoanDto>> getByProfileLastName(
      @RequestParam String lastName,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByProfileLastName(lastName, pageable));
  }

  @GetMapping("/jpql/by-category-and-state")
  public ResponseEntity<Page<LoanDto>> getByCategoryNameAndState(
      @RequestParam String category,
      @RequestParam String state,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryNameAndState(category, state, pageable));
  }

  @GetMapping("/jpql/by-username")
  public ResponseEntity<Page<LoanDto>> getByUsername(
      @RequestParam String username,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByUsername(username, pageable));
  }

  // ==================== Native Query эндпоинты (БЕЗ кэширования) ====================

  @GetMapping("/native/by-category")
  public ResponseEntity<Page<LoanDto>> getByCategoryNameNative(
      @RequestParam String name,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryNameNative(name, pageable));
  }

  @GetMapping("/native/by-lastname")
  public ResponseEntity<Page<LoanDto>> getByProfileLastNameNative(
      @RequestParam String lastName,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByProfileLastNameNative(lastName, pageable));
  }

  @GetMapping("/native/by-category-and-state")
  public ResponseEntity<Page<LoanDto>> getByCategoryNameAndStateNative(
      @RequestParam String category,
      @RequestParam String state,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryNameAndStateNative(category, state, pageable));
  }

  @GetMapping("/native/by-username")
  public ResponseEntity<Page<LoanDto>> getByUsernameNative(
      @RequestParam String username,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByUsernameNative(username, pageable));
  }

  // ==================== Управление кэшем ====================

  /**
   * Очистить кэш.
   * Пример: DELETE /api/loans/cache
   */
  @DeleteMapping("/cache")
  public ResponseEntity<String> clearCache() {
    loanService.clearCache();
    return ResponseEntity.ok("Cache cleared");
  }

  /**
   * Получить размер кэша.
   * Пример: GET /api/loans/cache/size
   */
  @GetMapping("/cache/size")
  public ResponseEntity<Integer> getCacheSize() {
    return ResponseEntity.ok(loanService.getCacheSize());
  }

  // ==================== CRUD операции ====================

  @PutMapping("/{id}")
  public ResponseEntity<LoanDto> update(@PathVariable Long id, @RequestBody LoanDto loanDto) {
    return ResponseEntity.ok(loanService.update(id, loanDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    loanService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
