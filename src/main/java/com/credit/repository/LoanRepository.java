package com.credit.repository;

import com.credit.model.Loan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

  @Override
  @EntityGraph(attributePaths = {"profile", "categories"})
  List<Loan> findAll();

  @Override
  @EntityGraph(attributePaths = {"profile", "categories"})
  Optional<Loan> findById(Long id);

  @EntityGraph(attributePaths = {"profile", "categories"})
  List<Loan> findByProfileId(Long profileId);

  @EntityGraph(attributePaths = {"profile", "categories"})
  List<Loan> findByCurrentState(String currentState);
}
