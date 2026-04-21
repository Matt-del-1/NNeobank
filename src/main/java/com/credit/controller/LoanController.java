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
   * Получить все кредиты с пагинацией.
   * Примеры:
   * - GET /api/loans
   * - GET /api/loans?page=0&size=5
   * - GET /api/loans?page=1&size=3&sort=amount,desc
   */
  @GetMapping
  public ResponseEntity<Page<LoanDto>> getAll(
      @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
    return ResponseEntity.ok(loanService.findAll(pageable));
  }

  /**
   * Найти кредиты по profileId с пагинацией.
   * Пример: GET /api/loans/profile/1?page=0&size=5&sort=amount,desc
   */
  @GetMapping("/profile/{profileId}")
  public ResponseEntity<Page<LoanDto>> getByProfile(
      @PathVariable Long profileId,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByProfileId(profileId, pageable));
  }

  /**
   * Найти кредиты по статусу с пагинацией.
   * Пример: GET /api/loans/filter?state=ACTIVE&page=0&size=5
   */
  @GetMapping("/filter")
  public ResponseEntity<Page<LoanDto>> getByState(
      @RequestParam String state,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByState(state, pageable));
  }

  // ==================== JPQL эндпоинты с пагинацией ====================

  /**
   * JPQL: Фильтрация по категории с пагинацией.
   * Пример: GET /api/loans/jpql/by-category?name=Ипотека&page=0&size=5&sort=amount,desc
   */
  @GetMapping("/jpql/by-category")
  public ResponseEntity<Page<LoanDto>> getByCategoryName(
      @RequestParam String name,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryName(name, pageable));
  }

  /**
   * JPQL: Фильтрация по фамилии с пагинацией.
   */
  @GetMapping("/jpql/by-lastname")
  public ResponseEntity<Page<LoanDto>> getByProfileLastName(
      @RequestParam String lastName,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByProfileLastName(lastName, pageable));
  }

  /**
   * JPQL: Составная фильтрация с пагинацией.
   */
  @GetMapping("/jpql/by-category-and-state")
  public ResponseEntity<Page<LoanDto>> getByCategoryNameAndState(
      @RequestParam String category,
      @RequestParam String state,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryNameAndState(category, state, pageable));
  }

  /**
   * JPQL: По username с пагинацией.
   */
  @GetMapping("/jpql/by-username")
  public ResponseEntity<Page<LoanDto>> getByUsername(
      @RequestParam String username,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByUsername(username, pageable));
  }

  // ==================== Native Query эндпоинты с пагинацией ====================

  /**
   * Native Query: Фильтрация по категории с пагинацией.
   */
  @GetMapping("/native/by-category")
  public ResponseEntity<Page<LoanDto>> getByCategoryNameNative(
      @RequestParam String name,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryNameNative(name, pageable));
  }

  /**
   * Native Query: Фильтрация по фамилии с пагинацией.
   */
  @GetMapping("/native/by-lastname")
  public ResponseEntity<Page<LoanDto>> getByProfileLastNameNative(
      @RequestParam String lastName,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByProfileLastNameNative(lastName, pageable));
  }

  /**
   * Native Query: Составная фильтрация с пагинацией.
   */
  @GetMapping("/native/by-category-and-state")
  public ResponseEntity<Page<LoanDto>> getByCategoryNameAndStateNative(
      @RequestParam String category,
      @RequestParam String state,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryNameAndStateNative(category, state, pageable));
  }

  /**
   * Native Query: По username с пагинацией.
   */
  @GetMapping("/native/by-username")
  public ResponseEntity<Page<LoanDto>> getByUsernameNative(
      @RequestParam String username,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByUsernameNative(username, pageable));
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
