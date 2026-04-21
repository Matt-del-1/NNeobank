package com.credit.service;

import com.credit.dto.LoanDto;
import com.credit.mapper.LoanMapper;
import com.credit.model.Category;
import com.credit.model.Loan;
import com.credit.model.Profile;
import com.credit.repository.CategoryRepository;
import com.credit.repository.LoanRepository;
import com.credit.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

  private final LoanRepository loanRepository;
  private final ProfileRepository profileRepository;
  private final CategoryRepository categoryRepository;
  private final LoanMapper loanMapper;

  @Transactional
  public LoanDto create(LoanDto dto) {
    Profile profile = profileRepository.findById(dto.getProfileId())
        .orElseThrow(() -> new RuntimeException("Profile not found with ID: " + dto.getProfileId()));

    Set<Category> categories = dto.getCategoryIds().stream()
        .map(id -> categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id)))
        .collect(Collectors.toSet());

    Loan loan = loanMapper.toEntity(dto);
    loan.setProfile(profile);
    loan.setCategories(categories);
    loan.setLastUpdate(LocalDateTime.now());

    Loan savedLoan = loanRepository.save(loan);
    return loanMapper.toDto(savedLoan);
  }

  // ==================== Методы с пагинацией ====================

  /**
   * Получить все кредиты с пагинацией.
   */
  @Transactional(readOnly = true)
  public Page<LoanDto> findAll(Pageable pageable) {
    return loanRepository.findAll(pageable)
        .map(loanMapper::toDto);
  }

  /**
   * Найти кредиты по profileId с пагинацией.
   */
  @Transactional(readOnly = true)
  public Page<LoanDto> findByProfileId(Long profileId, Pageable pageable) {
    return loanRepository.findByProfileId(profileId, pageable)
        .map(loanMapper::toDto);
  }

  /**
   * Найти кредиты по статусу с пагинацией.
   */
  @Transactional(readOnly = true)
  public Page<LoanDto> findByState(String state, Pageable pageable) {
    return loanRepository.findByCurrentState(state, pageable)
        .map(loanMapper::toDto);
  }

  // ==================== JPQL методы с пагинацией ====================

  @Transactional(readOnly = true)
  public Page<LoanDto> findByCategoryName(String categoryName, Pageable pageable) {
    return loanRepository.findByCategoryName(categoryName, pageable)
        .map(loanMapper::toDto);
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByProfileLastName(String lastName, Pageable pageable) {
    return loanRepository.findByProfileLastName(lastName, pageable)
        .map(loanMapper::toDto);
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByCategoryNameAndState(String categoryName, String state, Pageable pageable) {
    return loanRepository.findByCategoryNameAndState(categoryName, state, pageable)
        .map(loanMapper::toDto);
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByUsername(String username, Pageable pageable) {
    return loanRepository.findByUsername(username, pageable)
        .map(loanMapper::toDto);
  }

  // ==================== Native Query методы с пагинацией ====================

  @Transactional(readOnly = true)
  public Page<LoanDto> findByCategoryNameNative(String categoryName, Pageable pageable) {
    return loanRepository.findByCategoryNameNative(categoryName, pageable)
        .map(loanMapper::toDto);
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByProfileLastNameNative(String lastName, Pageable pageable) {
    return loanRepository.findByProfileLastNameNative(lastName, pageable)
        .map(loanMapper::toDto);
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByCategoryNameAndStateNative(String categoryName, String state, Pageable pageable) {
    return loanRepository.findByCategoryNameAndStateNative(categoryName, state, pageable)
        .map(loanMapper::toDto);
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByUsernameNative(String username, Pageable pageable) {
    return loanRepository.findByUsernameNative(username, pageable)
        .map(loanMapper::toDto);
  }

  // ==================== CRUD операции ====================

  @Transactional
  public LoanDto update(Long id, LoanDto dto) {
    Loan existingLoan = loanRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Loan not found"));

    existingLoan.setAmount(dto.getAmount());
    existingLoan.setCurrentState(dto.getCurrentState());
    existingLoan.setLastUpdate(LocalDateTime.now());

    return loanMapper.toDto(loanRepository.save(existingLoan));
  }

  @Transactional
  public void deleteById(Long id) {
    loanRepository.deleteById(id);
  }
}
