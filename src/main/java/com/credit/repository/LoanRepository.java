package com.credit.repository;

import com.credit.model.Loan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

  @Override
  @EntityGraph(attributePaths = {"profile", "categories"})
  List<Loan> findAll();

  List<Loan> findByStatusIgnoreCase(String status);
}