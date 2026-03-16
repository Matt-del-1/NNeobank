package com.credit.service;

import com.credit.dto.LoanDto;
import com.credit.model.Loan;
import com.credit.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {
  private final LoanRepository repository;

  public List<LoanDto> getAllLoans(String status) {
    List<Loan> loans = (status == null)
        ? repository.findAll()
        : repository.findByStatusIgnoreCase(status);

    return loans.stream()
        .map(l -> new LoanDto(l.getId(), l.getClientName(), l.getAmount(), l.getStatus()))
        .toList();
  }

  @Transactional
  public void createLoansWithCheck(List<Loan> loans) {
    for (Loan loan : loans) {
      repository.save(loan);
      if (loan.getAmount() > 1000000) {
        throw new RuntimeException("Ошибка: Сумма слишком велика. Откат всех сохранений.");
      }
    }
  }
}