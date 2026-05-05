package com.credit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoanServiceQueryTest {

  @Mock private LoanRepository loanRepository;
  @Mock private ProfileRepository profileRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private LoanMapper loanMapper;
  @Mock private LoanCache loanCache;

  @InjectMocks private LoanService loanService;

  private final Pageable pageable = PageRequest.of(0, 10);

  // ---------------- helpers ----------------

  private Loan loanFixture() {
    Set<Category> cats = new HashSet<>();
    cats.add(Category.builder().id(1L).name("M").build());
    return Loan.builder().id(1L).amount(1000.0).currentState("ACTIVE")
        .profile(Profile.builder().id(1L).build())
        .categories(cats).build();
  }

  private Page<Loan> onePageOfLoans() {
    return new PageImpl<>(List.of(loanFixture()));
  }

  // =========================================================
  //                       findAll
  // =========================================================

  @Test
  @DisplayName("findAll: cache miss — поход в репозиторий + put в кэш")
  void findAll_cacheMiss_goesToRepo() {
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(null);
    when(loanRepository.findAll(pageable)).thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    Page<LoanDto> result = loanService.findAll(pageable);

    assertEquals(1, result.getTotalElements());
    verify(loanRepository).findAll(pageable);
    verify(loanCache).put(any(LoanQueryKey.class), any());
  }

  @Test
  @DisplayName("findAll: cache hit — репозиторий не дёргается")
  void findAll_cacheHit_skipsRepo() {
    Page<LoanDto> cached = new PageImpl<>(List.of(LoanDto.builder().id(99L).build()));
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(cached);

    Page<LoanDto> result = loanService.findAll(pageable);

    assertSame(cached, result);
    assertEquals(99L, result.getContent().get(0).getId());
    verify(loanRepository, never()).findAll(any(Pageable.class));
    verify(loanCache, never()).put(any(), any());
  }

  // =========================================================
  //                    findByProfileId
  // =========================================================

  @Test
  @DisplayName("findByProfileId: cache miss — поход в репозиторий")
  void findByProfileId_miss() {
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(null);
    when(loanRepository.findByProfileId(1L, pageable)).thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    Page<LoanDto> result = loanService.findByProfileId(1L, pageable);

    assertEquals(1, result.getTotalElements());
    verify(loanRepository).findByProfileId(1L, pageable);
    verify(loanCache).put(any(LoanQueryKey.class), any());
  }

  @Test
  @DisplayName("findByProfileId: cache hit")
  void findByProfileId_hit() {
    Page<LoanDto> cached = new PageImpl<>(List.of(LoanDto.builder().id(7L).build()));
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(cached);

    Page<LoanDto> result = loanService.findByProfileId(1L, pageable);

    assertSame(cached, result);
    verify(loanRepository, never()).findByProfileId(any(), any());
  }

  // =========================================================
  //                       findByState
  // =========================================================

  @Test
  @DisplayName("findByState: cache miss — поход в репозиторий")
  void findByState_miss() {
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(null);
    when(loanRepository.findByCurrentState("ACTIVE", pageable)).thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    Page<LoanDto> result = loanService.findByState("ACTIVE", pageable);

    assertEquals(1, result.getTotalElements());
    verify(loanRepository).findByCurrentState("ACTIVE", pageable);
  }

  @Test
  @DisplayName("findByState: cache hit")
  void findByState_hit() {
    Page<LoanDto> cached = new PageImpl<>(List.of(new LoanDto()));
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(cached);

    loanService.findByState("ACTIVE", pageable);

    verify(loanRepository, never()).findByCurrentState(any(), any());
  }

  // =========================================================
  //                  findByCategoryName
  // =========================================================

  @Test
  @DisplayName("findByCategoryName: cache miss")
  void findByCategoryName_miss() {
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(null);
    when(loanRepository.findByCategoryName("M", pageable)).thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    loanService.findByCategoryName("M", pageable);

    verify(loanRepository).findByCategoryName("M", pageable);
    verify(loanCache).put(any(LoanQueryKey.class), any());
  }

  @Test
  @DisplayName("findByCategoryName: cache hit")
  void findByCategoryName_hit() {
    Page<LoanDto> cached = new PageImpl<>(List.of(new LoanDto()));
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(cached);

    loanService.findByCategoryName("M", pageable);

    verify(loanRepository, never()).findByCategoryName(any(), any());
  }

  // =========================================================
  //                findByProfileLastName
  // =========================================================

  @Test
  @DisplayName("findByProfileLastName: cache miss")
  void findByProfileLastName_miss() {
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(null);
    when(loanRepository.findByProfileLastName("Ivanov", pageable)).thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    loanService.findByProfileLastName("Ivanov", pageable);

    verify(loanRepository).findByProfileLastName("Ivanov", pageable);
  }

  @Test
  @DisplayName("findByProfileLastName: cache hit")
  void findByProfileLastName_hit() {
    Page<LoanDto> cached = new PageImpl<>(List.of(new LoanDto()));
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(cached);

    loanService.findByProfileLastName("Ivanov", pageable);

    verify(loanRepository, never()).findByProfileLastName(any(), any());
  }

  // =========================================================
  //              findByCategoryNameAndState
  // =========================================================

  @Test
  @DisplayName("findByCategoryNameAndState: cache miss")
  void findByCategoryNameAndState_miss() {
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(null);
    when(loanRepository.findByCategoryNameAndState("M", "ACTIVE", pageable))
        .thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    loanService.findByCategoryNameAndState("M", "ACTIVE", pageable);

    verify(loanRepository).findByCategoryNameAndState("M", "ACTIVE", pageable);
  }

  @Test
  @DisplayName("findByCategoryNameAndState: cache hit")
  void findByCategoryNameAndState_hit() {
    Page<LoanDto> cached = new PageImpl<>(List.of(new LoanDto()));
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(cached);

    loanService.findByCategoryNameAndState("M", "ACTIVE", pageable);

    verify(loanRepository, never()).findByCategoryNameAndState(any(), any(), any());
  }

  // =========================================================
  //                    findByUsername
  // =========================================================

  @Test
  @DisplayName("findByUsername: cache miss")
  void findByUsername_miss() {
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(null);
    when(loanRepository.findByUsername("ivan", pageable)).thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    loanService.findByUsername("ivan", pageable);

    verify(loanRepository).findByUsername("ivan", pageable);
  }

  @Test
  @DisplayName("findByUsername: cache hit")
  void findByUsername_hit() {
    Page<LoanDto> cached = new PageImpl<>(List.of(new LoanDto()));
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(cached);

    loanService.findByUsername("ivan", pageable);

    verify(loanRepository, never()).findByUsername(any(), any());
  }

  // =========================================================
  //                   native find* (без кэша)
  // =========================================================

  @Test
  @DisplayName("findByCategoryNameNative: всегда идёт в репозиторий, кэш не используется")
  void findByCategoryNameNative_alwaysHitsRepo() {
    when(loanRepository.findByCategoryNameNative("M", pageable)).thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    Page<LoanDto> result = loanService.findByCategoryNameNative("M", pageable);

    assertEquals(1, result.getTotalElements());
    verify(loanRepository).findByCategoryNameNative("M", pageable);
    verify(loanCache, never()).get(any());
    verify(loanCache, never()).put(any(), any());
  }

  @Test
  @DisplayName("findByProfileLastNameNative: всегда идёт в репозиторий, кэш не используется")
  void findByProfileLastNameNative_alwaysHitsRepo() {
    when(loanRepository.findByProfileLastNameNative("Ivanov", pageable))
        .thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    loanService.findByProfileLastNameNative("Ivanov", pageable);

    verify(loanRepository).findByProfileLastNameNative("Ivanov", pageable);
    verify(loanCache, never()).get(any());
  }

  @Test
  @DisplayName("findByCategoryNameAndStateNative: всегда идёт в репозиторий")
  void findByCategoryNameAndStateNative_alwaysHitsRepo() {
    when(loanRepository.findByCategoryNameAndStateNative("M", "ACTIVE", pageable))
        .thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    loanService.findByCategoryNameAndStateNative("M", "ACTIVE", pageable);

    verify(loanRepository).findByCategoryNameAndStateNative("M", "ACTIVE", pageable);
    verify(loanCache, never()).get(any());
  }

  @Test
  @DisplayName("findByUsernameNative: всегда идёт в репозиторий")
  void findByUsernameNative_alwaysHitsRepo() {
    when(loanRepository.findByUsernameNative("ivan", pageable)).thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    loanService.findByUsernameNative("ivan", pageable);

    verify(loanRepository).findByUsernameNative("ivan", pageable);
    verify(loanCache, never()).get(any());
  }

  // =========================================================
  //                clearCache / getCacheSize
  // =========================================================

  @Test
  @DisplayName("clearCache: делегирует loanCache.clear()")
  void clearCache_delegates() {
    loanService.clearCache();
    verify(loanCache, times(1)).clear();
  }

  @Test
  @DisplayName("getCacheSize: делегирует loanCache.size()")
  void getCacheSize_delegates() {
    when(loanCache.size()).thenReturn(42);

    int size = loanService.getCacheSize();

    assertEquals(42, size);
    verify(loanCache).size();
  }

  // =========================================================
  //         Вспомогательная проверка cache key
  // =========================================================

  @Test
  @DisplayName("findAll: ключ кэша строится с null-полями кроме pageable")
  void findAll_cacheKey_isCorrect() {
    when(loanCache.get(any(LoanQueryKey.class))).thenReturn(null);
    when(loanRepository.findAll(pageable)).thenReturn(onePageOfLoans());
    when(loanMapper.toDto(any(Loan.class))).thenReturn(new LoanDto());

    loanService.findAll(pageable);

    LoanQueryKey expected = new LoanQueryKey(null, null, null, null, null, pageable);
    verify(loanCache).get(eq(expected));
    verify(loanCache).put(eq(expected), any());
  }
}