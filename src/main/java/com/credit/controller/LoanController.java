package com.credit.controller;

import com.credit.dto.LoanDto;
import java.util.List;
import com.credit.service.LoanService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Validated
public class LoanController {

  private final LoanService loanService;

  @PostMapping
  public ResponseEntity<LoanDto> create(@Valid @RequestBody LoanDto loanDto) {
    return new ResponseEntity<>(loanService.create(loanDto), HttpStatus.CREATED);
  }

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
      @RequestParam @NotBlank(message = "state не может быть пустым") String state,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByState(state, pageable));
  }

  @GetMapping("/jpql/by-category")
  public ResponseEntity<Page<LoanDto>> getByCategoryName(
      @RequestParam @NotBlank(message = "name не может быть пустым") String name,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryName(name, pageable));
  }

  @GetMapping("/jpql/by-lastname")
  public ResponseEntity<Page<LoanDto>> getByProfileLastName(
      @RequestParam @NotBlank(message = "lastName не может быть пустым") String lastName,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByProfileLastName(lastName, pageable));
  }

  @GetMapping("/jpql/by-category-and-state")
  public ResponseEntity<Page<LoanDto>> getByCategoryNameAndState(
      @RequestParam @NotBlank(message = "category не может быть пустым") String category,
      @RequestParam @NotBlank(message = "state не может быть пустым") String state,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryNameAndState(category, state, pageable));
  }

  @GetMapping("/jpql/by-username")
  public ResponseEntity<Page<LoanDto>> getByUsername(
      @RequestParam @NotBlank(message = "username не может быть пустым") String username,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByUsername(username, pageable));
  }

  @GetMapping("/native/by-category")
  public ResponseEntity<Page<LoanDto>> getByCategoryNameNative(
      @RequestParam @NotBlank(message = "name не может быть пустым") String name,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByCategoryNameNative(name, pageable));
  }

  @GetMapping("/native/by-lastname")
  public ResponseEntity<Page<LoanDto>> getByProfileLastNameNative(
      @RequestParam @NotBlank(message = "lastName не может быть пустым") String lastName,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByProfileLastNameNative(lastName, pageable));
  }

  @GetMapping("/native/by-category-and-state")
  public ResponseEntity<Page<LoanDto>> getByCategoryNameAndStateNative(
      @RequestParam @NotBlank(message = "category не может быть пустым") String category,
      @RequestParam @NotBlank(message = "state не может быть пустым") String state,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(
        loanService.findByCategoryNameAndStateNative(category, state, pageable));
  }

  @GetMapping("/native/by-username")
  public ResponseEntity<Page<LoanDto>> getByUsernameNative(
      @RequestParam @NotBlank(message = "username не может быть пустым") String username,
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok(loanService.findByUsernameNative(username, pageable));
  }

  @DeleteMapping("/cache")
  public ResponseEntity<String> clearCache() {
    loanService.clearCache();
    return ResponseEntity.ok("Cache cleared");
  }

  @GetMapping("/cache/size")
  public ResponseEntity<Integer> getCacheSize() {
    return ResponseEntity.ok(loanService.getCacheSize());
  }

  @PutMapping("/{id}")
  public ResponseEntity<LoanDto> update(@PathVariable Long id,
      @Valid @RequestBody LoanDto loanDto) {
    return ResponseEntity.ok(loanService.update(id, loanDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    loanService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
  @PostMapping("/bulk")
  public ResponseEntity<List<LoanDto>> createBulk(@Valid @RequestBody List<LoanDto> dtos) {
    return new ResponseEntity<>(loanService.createBulk(dtos), HttpStatus.CREATED);
  }
}