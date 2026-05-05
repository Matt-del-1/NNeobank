package com.credit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.cache.LoanCache;
import com.credit.dto.CategoryDto;
import com.credit.dto.LoanDto;
import com.credit.dto.ProfileDto;
import com.credit.exception.BusinessException;
import com.credit.exception.NotFoundException;
import com.credit.mapper.LoanMapper;
import com.credit.model.Category;
import com.credit.model.Loan;
import com.credit.model.Profile;
import com.credit.repository.CategoryRepository;
import com.credit.repository.LoanRepository;
import com.credit.repository.ProfileRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoanServiceBulkTest {

  @Mock private LoanRepository loanRepository;
  @Mock private ProfileRepository profileRepository;
  @Mock private CategoryRepository categoryRepository;
  @Mock private LoanMapper loanMapper;
  @Mock private LoanCache loanCache;

  @InjectMocks private LoanService loanService;

  private Profile profile;
  private Category category;

  @BeforeEach
  void setUp() {
    profile = Profile.builder().id(1L).firstName("Ivan").lastName("Ivanov").build();
    category = Category.builder().id(1L).name("Mortgage").rate(7.5f).build();
  }

  // ---------------- helpers ----------------

  private LoanDto fullDto(Double amount, String state) {
    return LoanDto.builder()
        .amount(amount).currentState(state)
        .profile(ProfileDto.builder().id(1L).build())
        .categories(Set.of(CategoryDto.builder().id(1L).build()))
        .build();
  }

  private Loan savedLoan(Long id) {
    Set<Category> cats = new HashSet<>();
    cats.add(category);
    return Loan.builder().id(id).amount(1000.0).currentState("ACTIVE")
        .profile(profile).categories(cats).build();
  }

  private LoanDto savedDtoWithProfile(Long id, Long profileId) {
    return LoanDto.builder()
        .id(id)
        .profile(ProfileDto.builder().id(profileId).build())
        .build();
  }

  // =========================================================
  //                 createBulk: happy path
  // =========================================================

  @Test
  @DisplayName("createBulk: 2 валидных DTO — оба сохраняются, "
      + "invalidateByProfileId вызывается по первому, кэш чистится")
  void createBulk_allValid_savesAll() {
    List<LoanDto> input = List.of(
        fullDto(1000.0, "ACTIVE"),
        fullDto(2000.0, "ACTIVE")
    );

    when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(loanMapper.toEntity(any(LoanDto.class))).thenReturn(new Loan());
    when(loanRepository.saveAndFlush(any(Loan.class))).thenReturn(savedLoan(10L));
    when(loanMapper.toDto(any(Loan.class))).thenReturn(savedDtoWithProfile(10L, 1L));

    List<LoanDto> result = loanService.createBulk(input);

    assertEquals(2, result.size());
    verify(loanRepository, times(2)).saveAndFlush(any(Loan.class));
    verify(loanCache).invalidateByProfileId(1L);
    verify(loanCache).clear();
  }

  @Test
  @DisplayName("createBulk: пустой список — ничего не сохраняется, "
      + "invalidateByProfileId не вызывается, но clear вызывается")
  void createBulk_emptyList_clearsOnly() {
    List<LoanDto> result = loanService.createBulk(List.of());

    assertEquals(0, result.size());
    verify(loanRepository, never()).saveAndFlush(any(Loan.class));
    verify(loanCache, never()).invalidateByProfileId(any());
    verify(loanCache).clear();
  }

  // =========================================================
  //         validateAndBuildLoan: ветки amount
  // =========================================================

  @Test
  @DisplayName("createBulk: amount == null — BusinessException, ничего не сохранено, кэш не очищен")
  void createBulk_nullAmount_throws() {
    List<LoanDto> input = List.of(
        LoanDto.builder().amount(null).currentState("ACTIVE")
            .profile(ProfileDto.builder().id(1L).build())
            .categories(Set.of(CategoryDto.builder().id(1L).build()))
            .build()
    );

    assertThrows(BusinessException.class, () -> loanService.createBulk(input));
    verify(loanRepository, never()).saveAndFlush(any(Loan.class));
    verify(loanCache, never()).clear();
  }

  @Test
  @DisplayName("createBulk: amount == 0 — BusinessException")
  void createBulk_zeroAmount_throws() {
    List<LoanDto> input = List.of(
        LoanDto.builder().amount(0.0).currentState("ACTIVE")
            .profile(ProfileDto.builder().id(1L).build())
            .categories(Set.of(CategoryDto.builder().id(1L).build()))
            .build()
    );

    assertThrows(BusinessException.class, () -> loanService.createBulk(input));
    verify(loanRepository, never()).saveAndFlush(any(Loan.class));
  }

  @Test
  @DisplayName("createBulk: amount < 0 — BusinessException")
  void createBulk_negativeAmount_throws() {
    List<LoanDto> input = List.of(
        LoanDto.builder().amount(-1.0).currentState("ACTIVE")
            .profile(ProfileDto.builder().id(1L).build())
            .categories(Set.of(CategoryDto.builder().id(1L).build()))
            .build()
    );

    assertThrows(BusinessException.class, () -> loanService.createBulk(input));
    verify(loanRepository, never()).saveAndFlush(any(Loan.class));
  }

  // =========================================================
  //       validateAndBuildLoan: ветка currentState=FAIL
  // =========================================================

  @Test
  @DisplayName("createBulk: currentState='FAIL' во втором DTO — BusinessException, "
      + "первый займ уже сохранён (откат пойдёт через @Transactional)")
  void createBulk_failState_throwsAfterFirstSaved() {
    List<LoanDto> input = List.of(
        fullDto(1000.0, "ACTIVE"),
        fullDto(2000.0, "FAIL")
    );

    when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(loanMapper.toEntity(any(LoanDto.class))).thenReturn(new Loan());
    when(loanRepository.saveAndFlush(any(Loan.class))).thenReturn(savedLoan(10L));
    when(loanMapper.toDto(any(Loan.class))).thenReturn(savedDtoWithProfile(10L, 1L));

    assertThrows(BusinessException.class, () -> loanService.createBulk(input));

    // первый успел сохраниться до выброса
    verify(loanRepository, times(1)).saveAndFlush(any(Loan.class));
    // clear() и invalidateByProfileId() не должны вызваться, т.к. упало до них
    verify(loanCache, never()).clear();
    verify(loanCache, never()).invalidateByProfileId(any());
  }

  @Test
  @DisplayName("createBulk: 'fail' (lowercase) — тоже BusinessException (equalsIgnoreCase)")
  void createBulk_failStateLowercase_throws() {
    List<LoanDto> input = List.of(fullDto(1000.0, "fail"));

    when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

    assertThrows(BusinessException.class, () -> loanService.createBulk(input));
    verify(loanRepository, never()).saveAndFlush(any(Loan.class));
  }

  // =========================================================
  //     validateAndBuildLoan: ветки profile / categories
  // =========================================================

  @Test
  @DisplayName("createBulk: profile == null в DTO — сохраняется без профиля, "
      + "invalidateByProfileId не вызывается")
  void createBulk_nullProfileInDto_savesWithoutProfile() {
    List<LoanDto> input = List.of(
        LoanDto.builder().amount(1000.0).currentState("ACTIVE")
            .profile(null).categories(null).build()
    );

    when(loanMapper.toEntity(any(LoanDto.class))).thenReturn(new Loan());
    when(loanRepository.saveAndFlush(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
    when(loanMapper.toDto(any(Loan.class)))
        .thenReturn(LoanDto.builder().profile(null).build());

    loanService.createBulk(input);

    verify(profileRepository, never()).findById(any());
    verify(loanRepository, times(1)).saveAndFlush(any(Loan.class));
    verify(loanCache, never()).invalidateByProfileId(any());
    verify(loanCache).clear();
  }

  @Test
  @DisplayName("createBulk: profile.id указан, но не найден — NotFoundException")
  void createBulk_profileNotFound_throws() {
    List<LoanDto> input = List.of(fullDto(1000.0, "ACTIVE"));

    when(profileRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> loanService.createBulk(input));
    verify(loanRepository, never()).saveAndFlush(any(Loan.class));
  }

  @Test
  @DisplayName("createBulk: categories == null — сохраняется с пустым набором категорий")
  void createBulk_nullCategories_savesWithEmptySet() {
    List<LoanDto> input = List.of(
        LoanDto.builder().amount(1000.0).currentState("ACTIVE")
            .profile(ProfileDto.builder().id(1L).build())
            .categories(null)
            .build()
    );

    when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    when(loanMapper.toEntity(any(LoanDto.class))).thenReturn(new Loan());
    when(loanRepository.saveAndFlush(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
    when(loanMapper.toDto(any(Loan.class)))
        .thenReturn(savedDtoWithProfile(10L, 1L));

    loanService.createBulk(input);

    verify(categoryRepository, never()).findById(any());
    verify(loanRepository, times(1)).saveAndFlush(any(Loan.class));
  }

  @Test
  @DisplayName("createBulk: category не найдена — NotFoundException")
  void createBulk_categoryNotFound_throws() {
    List<LoanDto> input = List.of(fullDto(1000.0, "ACTIVE"));

    when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> loanService.createBulk(input));
    verify(loanRepository, never()).saveAndFlush(any(Loan.class));
  }

  // =========================================================
  //   createBulk: ветки результирующего invalidateByProfileId
  // =========================================================

  @Test
  @DisplayName("createBulk: первый сохранённый DTO имеет profile.id — "
      + "invalidateByProfileId вызывается ровно с этим id")
  void createBulk_firstHasProfileId_invalidatesById() {
    List<LoanDto> input = List.of(fullDto(1000.0, "ACTIVE"));

    when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(loanMapper.toEntity(any(LoanDto.class))).thenReturn(new Loan());
    when(loanRepository.saveAndFlush(any(Loan.class))).thenReturn(savedLoan(10L));
    when(loanMapper.toDto(any(Loan.class))).thenReturn(savedDtoWithProfile(10L, 1L));

    loanService.createBulk(input);

    verify(loanCache, times(1)).invalidateByProfileId(1L);
    verify(loanCache).clear();
  }

  @Test
  @DisplayName("createBulk: первый сохранённый DTO без profile — "
      + "invalidateByProfileId НЕ вызывается, но clear вызывается")
  void createBulk_firstWithoutProfile_skipsInvalidate() {
    List<LoanDto> input = List.of(
        LoanDto.builder().amount(1000.0).currentState("ACTIVE")
            .profile(null).categories(null).build()
    );

    when(loanMapper.toEntity(any(LoanDto.class))).thenReturn(new Loan());
    when(loanRepository.saveAndFlush(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
    // в результирующем DTO profile == null
    when(loanMapper.toDto(any(Loan.class)))
        .thenReturn(LoanDto.builder().profile(null).build());

    loanService.createBulk(input);

    verify(loanCache, never()).invalidateByProfileId(any());
    verify(loanCache).clear();
  }
}
