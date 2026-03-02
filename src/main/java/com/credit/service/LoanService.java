package com.credit.service;

import com.credit.model.Loan;
import com.credit.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
    private final LoanRepository repository;

    public List<Loan> getAllLoans(String status) {
        if (status == null) return repository.findAll();
        return repository.findAll().stream()
                .filter(l -> l.getStatus().equalsIgnoreCase(status))
                .toList();
    }

    public Loan getLoanById(Long id) {
        return repository.findById(id).orElseThrow();
    }
}