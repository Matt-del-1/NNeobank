package com.credit.service;

import com.credit.dto.LoanDto;
import com.credit.mapper.LoanMapper;
import com.credit.model.Category;
import com.credit.model.Loan;
import com.credit.model.Profile;
import com.credit.repository.CategoryRepository;
import com.credit.repository.LoanRepository;
import com.credit.repository.ProfileRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        .orElseThrow(() -> new RuntimeException("Profile not found withID: " + dto.getProfileId()));

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

  @Transactional(readOnly = true)
  public LoanDto findById(Long id) {
    return loanRepository.findById(id)
        .map(loanMapper::toDto)
        .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + id));
  }

  @Transactional(readOnly = true)
  public List<LoanDto> findAll() {
    return loanRepository.findAll().stream()
        .map(loanMapper::toDto)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<LoanDto> findByProfileId(Long profileId) {
    return loanRepository.findByProfileId(profileId).stream()
        .map(loanMapper::toDto)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<LoanDto> findByState(String state) {
    return loanRepository.findByCurrentState(state).stream()
        .map(loanMapper::toDto)
        .toList();
  }

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

  @Transactional
  public LoanDto createWithFailure(LoanDto dto) {
    Profile profile = profileRepository.findById(dto.getProfileId())
        .orElseThrow(() -> new RuntimeException("Profile not found"));

    Loan loan = loanMapper.toEntity(dto);
    loan.setProfile(profile);
    loan.setLastUpdate(LocalDateTime.now());

    Loan savedLoan = loanRepository.save(loan);


    return loanMapper.toDto(savedLoan);
  }
}
