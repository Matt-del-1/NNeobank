package com.credit.component;

import com.credit.model.Loan;
import com.credit.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
  private final LoanRepository repository;

  private static final double VAL_1 = 50000.0;
  private static final double VAL_2 = 150000.0;

  @Override
  public void run(String... args) {
    repository.save(Loan.builder().clientName("Ivanov").amount(VAL_1).status("APPROVED").build());
    repository.save(Loan.builder().clientName("Petrov").amount(VAL_2).status("PENDING").build());
    log.info(">> База данных инициализирована через JPA.");
  }
}