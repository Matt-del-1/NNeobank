package com.credit.repository;

import com.credit.model.Loan;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class LoanRepository {
    // Это и есть наша "база данных" на время первой лабы
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