package com.credit.repository;

import com.credit.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

  // ==================== Базовые методы с пагинацией ====================

  @Override
  @EntityGraph(attributePaths = {"profile", "categories"})
  Page<Loan> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"profile", "categories"})
  Page<Loan> findByProfileId(Long profileId, Pageable pageable);

  @EntityGraph(attributePaths = {"profile", "categories"})
  Page<Loan> findByCurrentState(String currentState, Pageable pageable);

  // ==================== JPQL запросы с пагинацией ====================

  /**
   * JPQL: Найти все кредиты по имени категории с пагинацией.
   */
  @Query("SELECT l FROM Loan l JOIN l.categories c WHERE LOWER(c.name) = LOWER(:categoryName)")
  @EntityGraph(attributePaths = {"profile", "categories"})
  Page<Loan> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

  /**
   * JPQL: Найти кредиты по фамилии клиента с пагинацией.
   */
  @Query("SELECT l FROM Loan l JOIN l.profile p WHERE LOWER(p.lastName) = LOWER(:lastName)")
  @EntityGraph(attributePaths = {"profile", "categories"})
  Page<Loan> findByProfileLastName(@Param("lastName") String lastName, Pageable pageable);

  /**
   * JPQL: Сложный запрос - кредиты по категории И статусу с пагинацией.
   */
  @Query("SELECT l FROM Loan l JOIN l.categories c " +
         "WHERE LOWER(c.name) = LOWER(:categoryName) AND l.currentState = :state")
  @EntityGraph(attributePaths = {"profile", "categories"})
  Page<Loan> findByCategoryNameAndState(
      @Param("categoryName") String categoryName,
      @Param("state") String state,
      Pageable pageable);

  /**
   * JPQL: Найти кредиты по username пользователя с пагинацией.
   */
  @Query("SELECT l FROM Loan l JOIN l.profile p JOIN p.user u WHERE u.username = :username")
  @EntityGraph(attributePaths = {"profile", "categories"})
  Page<Loan> findByUsername(@Param("username") String username, Pageable pageable);

  // ==================== Native Query запросы (БЕЗ EntityGraph, С пагинацией) ====================

  /**
   * Native Query: Найти кредиты по имени категории с пагинацией.
   * Используем COUNT_QUERY для корректного подсчёта общего количества записей.
   */
  @Query(value =
      "SELECT l.* FROM loans l " +
      "JOIN loan_categories_map lcm ON l.id = lcm.loan_id " +
      "JOIN categories c ON lcm.category_id = c.id " +
      "WHERE LOWER(c.name) = LOWER(:categoryName)",
      countQuery =
          "SELECT COUNT(l.id) FROM loans l " +
          "JOIN loan_categories_map lcm ON l.id = lcm.loan_id " +
          "JOIN categories c ON lcm.category_id = c.id " +
          "WHERE LOWER(c.name) = LOWER(:categoryName)",
      nativeQuery = true)
  Page<Loan> findByCategoryNameNative(@Param("categoryName") String categoryName, Pageable pageable);

  /**
   * Native Query: Найти кредиты по фамилии клиента с пагинацией.
   */
  @Query(value =
      "SELECT l.* FROM loans l " +
      "JOIN profiles p ON l.client_id = p.id " +
      "WHERE LOWER(p.last_name) = LOWER(:lastName)",
      countQuery =
          "SELECT COUNT(l.id) FROM loans l " +
          "JOIN profiles p ON l.client_id = p.id " +
          "WHERE LOWER(p.last_name) = LOWER(:lastName)",
      nativeQuery = true)
  Page<Loan> findByProfileLastNameNative(@Param("lastName") String lastName, Pageable pageable);

  /**
   * Native Query: Составной запрос с пагинацией.
   */
  @Query(value =
      "SELECT l.* FROM loans l " +
      "JOIN loan_categories_map lcm ON l.id = lcm.loan_id " +
      "JOIN categories c ON lcm.category_id = c.id " +
      "WHERE LOWER(c.name) = LOWER(:categoryName) AND l.current_state = :state",
      countQuery =
          "SELECT COUNT(l.id) FROM loans l " +
          "JOIN loan_categories_map lcm ON l.id = lcm.loan_id " +
          "JOIN categories c ON lcm.category_id = c.id " +
          "WHERE LOWER(c.name) = LOWER(:categoryName) AND l.current_state = :state",
      nativeQuery = true)
  Page<Loan> findByCategoryNameAndStateNative(
      @Param("categoryName") String categoryName,
      @Param("state") String state,
      Pageable pageable);

  /**
   * Native Query: Найти кредиты по username пользователя с пагинацией.
   */
  @Query(value =
      "SELECT l.* FROM loans l " +
      "JOIN profiles p ON l.client_id = p.id " +
      "JOIN users u ON p.user_id = u.id " +
      "WHERE u.username = :username",
      countQuery =
          "SELECT COUNT(l.id) FROM loans l " +
          "JOIN profiles p ON l.client_id = p.id " +
          "JOIN users u ON p.user_id = u.id " +
          "WHERE u.username = :username",
      nativeQuery = true)
  Page<Loan> findByUsernameNative(@Param("username") String username, Pageable pageable);
}
