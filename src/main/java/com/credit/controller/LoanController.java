package com.credit.controller;

import com.credit.model.Loan;
import com.credit.service.LoanService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

  private final LoanService service;

  @GetMapping
  public List<Loan> list(@RequestParam(required = false) String status) {
    return service.getAllLoans(status);
  }

  @GetMapping("/{id}")
  public Loan getOne(@PathVariable Long id) {
    return service.getLoanById(id);
  }
}