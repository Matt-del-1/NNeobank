package com.credit.repository;

import com.credit.model.Loan;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class LoanRepository {

  // Временная  "база данных"
  private final Map<Long, Loan> storage = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  public Loan save(Loan loan) {
    if (loan.getId() == null) {
      loan.setId(idGenerator.getAndIncrement());
    }
    storage.put(loan.getId(), loan);
    return loan;
  }

  public Optional<Loan> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  public List<Loan> findAll() {
    return new ArrayList<>(storage.values());
  }
}