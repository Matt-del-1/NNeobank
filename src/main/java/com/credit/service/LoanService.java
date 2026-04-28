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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        .orElseThrow(
            () -> new RuntimeException("Profile not found with ID: " + dto.getProfileId()));

    Set<Category> categories = dto.getCategoryIds().stream()
        .map(id -> categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id)))
        .collect(Collectors.toSet());

    Loan loan = loanMapper.toEntity(dto);
    loan.setProfile(profile);
    loan.setCategories(categories);
    loan.setLastUpdate(LocalDateTime.now());

    Loan savedLoan = loanRepository.save(loan);

    invalidateCacheForNewLoan(savedLoan);

    return loanMapper.toDto(savedLoan);
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findAll(Pageable pageable) {
    LoanQueryKey key = new LoanQueryKey(null, null, null, null, null, pageable);

    Page<LoanDto> cached = loanCache.get(key);
    if (cached != null) {
      return cached;
    }

    Page<LoanDto> result = loanRepository.findAll(pageable).map(loanMapper::toDto);
    loanCache.put(key, result);

    return result;
  }

  @Transactional(readOnly = true)
  public Page<LoanDto> findByProfileId(Long profileId, Pageable pageable) {
    LoanQueryKey key = new LoanQueryKey(profileId, null, null, null, null, pageable);

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
    LoanQueryKey key = new LoanQueryKey(null, null, null, state, null, pageable);

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
    LoanQueryKey key = new LoanQueryKey(null, categoryName, null, null, null, pageable);

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
    LoanQueryKey key = new LoanQueryKey(null, null, lastName, null, null, pageable);

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
  public Page<LoanDto> findByCategoryNameAndState(String categoryName, String state,
      Pageable pageable) {
    LoanQueryKey key = new LoanQueryKey(null, categoryName, null, state, null, pageable);

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
    LoanQueryKey key = new LoanQueryKey(null, null, null, null, username, pageable);

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
  public Page<LoanDto> findByCategoryNameAndStateNative(String categoryName, String state,
      Pageable pageable) {
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

    Long profileId = existingLoan.getProfile() != null ? existingLoan.getProfile().getId() : null;
    String oldState = existingLoan.getCurrentState();
    Set<String> oldCategories = existingLoan.getCategories().stream()
        .map(Category::getName)
        .collect(Collectors.toSet());

    existingLoan.setAmount(dto.getAmount());
    existingLoan.setCurrentState(dto.getCurrentState());
    existingLoan.setLastUpdate(LocalDateTime.now());

    Loan updated = loanRepository.save(existingLoan);
    LoanDto result = loanMapper.toDto(updated);

    invalidateCacheForUpdate(profileId, oldState, oldCategories, updated);

    return result;
  }

  @Transactional
  public void deleteById(Long id) {
    Loan loan = loanRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Loan not found"));

    Long profileId = loan.getProfile() != null ? loan.getProfile().getId() : null;
    String state = loan.getCurrentState();
    Set<String> categories = loan.getCategories().stream()
        .map(Category::getName)
        .collect(Collectors.toSet());

    loanRepository.deleteById(id);

    invalidateCacheForDeletedLoan(profileId, state, categories);
  }

  private void invalidateCacheForNewLoan(Loan loan) {
    if (loan.getProfile() != null) {
      loanCache.invalidateByProfileId(loan.getProfile().getId());
    }
    for (Category category : loan.getCategories()) {
      loanCache.invalidateByCategory(category.getName());
    }
    if (loan.getCurrentState() != null) {
      loanCache.invalidateByState(loan.getCurrentState());
    }
    loanCache.clear();
  }

  private void invalidateCacheForUpdate(Long profileId, String oldState,
      Set<String> oldCategories, Loan updatedLoan) {
    if (profileId != null) {
      loanCache.invalidateByProfileId(profileId);
    }

    String newState = updatedLoan.getCurrentState();
    if (oldState != null && !oldState.equals(newState)) {
      loanCache.invalidateByState(oldState);
      loanCache.invalidateByState(newState);
    } else if (newState != null) {
      loanCache.invalidateByState(newState);
    }

    Set<String> newCategories = updatedLoan.getCategories().stream()
        .map(Category::getName)
        .collect(Collectors.toSet());

    oldCategories.forEach(loanCache::invalidateByCategory);
    newCategories.forEach(loanCache::invalidateByCategory);
  }

  private void invalidateCacheForDeletedLoan(Long profileId, String state, Set<String> categories) {
    if (profileId != null) {
      loanCache.invalidateByProfileId(profileId);
    }
    if (state != null) {
      loanCache.invalidateByState(state);
    }
    categories.forEach(loanCache::invalidateByCategory);
    loanCache.clear();
  }

  public void clearCache() {
    loanCache.clear();
  }

  public int getCacheSize() {
    return loanCache.size();
  }
}
