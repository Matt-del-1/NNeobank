package com.credit.controller;

import com.credit.dto.LoanDto;
import com.credit.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
  private final LoanService service;

  @GetMapping
  public List<LoanDto> getLoans(@RequestParam(required = false) String status) {
    return service.getAllLoans(status);
  }
}