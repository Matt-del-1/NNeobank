package com.credit.service;

import com.credit.cache.LoanCache;
import com.credit.cache.LoanQueryKey;
import com.credit.dto.LoanDto;
import com.credit.mapper.LoanMapper;
import com.credit.model.Category;
import com.credit.model.Loan;
import com.credit.model.Profile;
import com.credit.repository.CategoryRepository;
import com.credit.repository.LoanRepository;
import com.credit.repository.ProfileRepository;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {

  private final LoanRepository loanRepository;
  private final ProfileRepository profileRepository;
  private final CategoryRepository categoryRepository;
  private final LoanMapper loanMapper;
  private final LoanCache loanCache;

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

    loanCache.invalidateByProfileId(profile.getId());

    return loanMapper.toDto(savedLoan);
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findAll(Pageable pageable) {
    LoanQueryKey key = LoanCache.createKeyForAll(pageable);

    // Проверяем кэш
    Page<LoanDto> cached = loanCache.get(key);
    if (cached != null) {
      return cached;
    }

    Page<LoanDto> result = loanRepository.findAll(pageable)
        .map(loanMapper::toDto);

    loanCache.put(key, result);
    return result;
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByProfileId(Long profileId, Pageable pageable) {
    LoanQueryKey key = LoanCache.createKeyForProfile(profileId, pageable);

    Page<LoanDto> cached = loanCache.get(key);
    if (cached != null) {
      return cached;
    }

    Page<LoanDto> result = loanRepository.findByProfileId(profileId, pageable)
        .map(loanMapper::toDto);

    loanCache.put(key, result);

    return result;
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByState(String state, Pageable pageable) {
    LoanQueryKey key = LoanCache.createKeyForState(state, pageable);

    Page<LoanDto> cached = loanCache.get(key);
    if (cached != null) {
      return cached;
    }

    Page<LoanDto> result = loanRepository.findByCurrentState(state, pageable)
        .map(loanMapper::toDto);

    loanCache.put(key, result);

    return result;
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByCategoryName(String categoryName, Pageable pageable) {
    LoanQueryKey key = LoanCache.createKeyForCategory(categoryName, pageable);

    Page<LoanDto> cached = loanCache.get(key);
    if (cached != null) {
      return cached;
    }

    Page<LoanDto> result = loanRepository.findByCategoryName(categoryName, pageable)
        .map(loanMapper::toDto);

    loanCache.put(key, result);

    return result;
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByProfileLastName(String lastName, Pageable pageable) {
    LoanQueryKey key = LoanCache.createKeyForLastName(lastName, pageable);

    Page<LoanDto> cached = loanCache.get(key);
    if (cached != null) {
      return cached;
    }

    Page<LoanDto> result = loanRepository.findByProfileLastName(lastName, pageable)
        .map(loanMapper::toDto);

    loanCache.put(key, result);

    return result;
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByCategoryNameAndState(String categoryName, String state, Pageable pageable) {
    LoanQueryKey key = LoanCache.createKeyForCategoryAndState(categoryName, state, pageable);

    Page<LoanDto> cached = loanCache.get(key);
    if (cached != null) {
      return cached;
    }

    Page<LoanDto> result = loanRepository.findByCategoryNameAndState(categoryName, state, pageable)
        .map(loanMapper::toDto);

    loanCache.put(key, result);

    return result;
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByUsername(String username, Pageable pageable) {
    LoanQueryKey key = LoanCache.createKeyForUsername(username, pageable);

    Page<LoanDto> cached = loanCache.get(key);
    if (cached != null) {
      return cached;
    }

    Page<LoanDto> result = loanRepository.findByUsername(username, pageable)
        .map(loanMapper::toDto);

    loanCache.put(key, result);

    return result;
  }

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

  @Transactional
  public LoanDto update(Long id, LoanDto dto) {
    Loan existingLoan = loanRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Loan not found"));

    existingLoan.setAmount(dto.getAmount());
    existingLoan.setCurrentState(dto.getCurrentState());
    existingLoan.setLastUpdate(LocalDateTime.now());

    Long profileId = existingLoan.getProfile() != null ? existingLoan.getProfile().getId() : null;

    LoanDto result = loanMapper.toDto(loanRepository.save(existingLoan));

    if (profileId != null) {
      loanCache.invalidateByProfileId(profileId);
    }
    loanCache.clear();
    return result;
  }

  @Transactional
  public void deleteById(Long id) {
    Loan loan = loanRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Loan not found"));

    Long profileId = loan.getProfile() != null ? loan.getProfile().getId() : null;

    loanRepository.deleteById(id);

    if (profileId != null) {
      loanCache.invalidateByProfileId(profileId);
    }
    loanCache.clear();
  }
  public void clearCache() {
    loanCache.clear();
  }
  public int getCacheSize() {
    return loanCache.size();
  }
}
