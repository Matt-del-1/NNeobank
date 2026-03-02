package com.credit.component;

import com.credit.model.Loan;
import com.credit.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

  private final LoanRepository repository;

  @Override
  public void run(String... args) {
    // Наполняем репозиторий при старте
    repository.save(new Loan(null, "Ivan Ivanov", 50000.0, "APPROVED"));
    repository.save(new Loan(null, "Petr Petrov", 1500000.0, "REJECTED"));
    repository.save(new Loan(null, "Sidor Sidorov", 250000.0, "PENDING"));

    System.out.println(">> Database pre-loaded with 3 loans.");
  }
}