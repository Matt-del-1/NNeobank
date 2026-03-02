package com.credit.controller;

import com.credit.model.Loan;
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
    public List<Loan> list(@RequestParam(required = false) String status) {
        return service.getAllLoans(status);
    }

    @GetMapping("/{id}")
    public Loan getOne(@PathVariable Long id) {
        return service.getLoanById(id);
    }
}