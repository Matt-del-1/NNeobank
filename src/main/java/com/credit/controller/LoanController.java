package com.credit.controller;

import com.credit.dto.LoanDto;
import com.credit.service.LoanService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

  private final LoanService loanService;

  @PostMapping
  public ResponseEntity<LoanDto> create(@RequestBody LoanDto loanDto) {
    return new ResponseEntity<>(loanService.create(loanDto), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<LoanDto>> getAll() {
    return ResponseEntity.ok(loanService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<LoanDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(loanService.findById(id));
  }

  @GetMapping("/profile/{profileId}")
  public ResponseEntity<List<LoanDto>> getByProfile(@PathVariable Long profileId) {
    return ResponseEntity.ok(loanService.findByProfileId(profileId));
  }

  @GetMapping("/filter")
  public ResponseEntity<List<LoanDto>> getByState(@RequestParam String state) {
    return ResponseEntity.ok(loanService.findByState(state));
  }

  @PutMapping("/{id}")
  public ResponseEntity<LoanDto> update(@PathVariable Long id, @RequestBody LoanDto loanDto) {
    return ResponseEntity.ok(loanService.update(id, loanDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    loanService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/test-rollback")
  public ResponseEntity<LoanDto> createWithRollback(@RequestBody LoanDto loanDto) {
    return ResponseEntity.ok(loanService.createWithFailure(loanDto));
  }
}
