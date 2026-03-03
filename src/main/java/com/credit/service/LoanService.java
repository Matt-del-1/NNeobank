package com.credit.service;

import com.credit.dto.LoanDto;
import com.credit.model.Loan;
import com.credit.repository.LoanRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {

  private final LoanRepository repository;

  public List<LoanDto> getAllLoans(String status, Double amount) {
    return repository.findAll().stream()
        // Фильтрация по статусу (если передан)
        .filter(l -> status == null || l.getStatus().equalsIgnoreCase(status))
        // Фильтрация по сумме (если передана)
        .filter(l -> amount == null || l.getAmount().equals(amount))
        // Трансформация в DTO
        .map(this::convertToDto)
        .toList();
  }

  public LoanDto getLoanById(Long id) {
    return repository.findById(id)
        .map(this::convertToDto)
        .orElseThrow(() -> new RuntimeException("Loan not found"));
  }

  private LoanDto convertToDto(Loan loan) {
    return new LoanDto(
        loan.getId(),
        loan.getClientName(),
        loan.getAmount(),
        loan.getStatus()
    );
  }
}