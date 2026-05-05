package com.credit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.cache.LoanCache;
import com.credit.dto.CategoryDto;
import com.credit.dto.LoanDto;
import com.credit.dto.ProfileDto;
import com.credit.exception.NotFoundException;
import com.credit.mapper.LoanMapper;
import com.credit.model.Category;
import com.credit.model.Loan;
import com.credit.model.Profile;
import com.credit.repository.CategoryRepository;
import com.credit.repository.LoanRepository;
import com.credit.repository.ProfileRepository;
import java.util.HashSet;
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
class LoanServiceCrudTest {

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

  private LoanDto buildDto(Double amount, String state) {
    return LoanDto.builder()
        .amount(amount).currentState(state)
        .profile(ProfileDto.builder().id(1L).build())
        .categories(Set.of(CategoryDto.builder().id(1L).build()))
        .build();
  }

  private Loan buildLoan(Long id, String state) {
    Set<Category> cats = new HashSet<>();
    cats.add(category);
    return Loan.builder().id(id).amount(1000.0).currentState(state)
        .profile(profile).categories(cats).build();
  }

  // =========================================================
  //                       create
  // =========================================================

  @Test
  @DisplayName("create: валидный DTO — займ сохраняется, кэш чистится")
  void create_valid_savesAndInvalidatesCache() {
    LoanDto input = buildDto(5000.0, "ACTIVE");
    Loan saved = buildLoan(10L, "ACTIVE");

    when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(loanMapper.toEntity(input)).thenReturn(new Loan());
    when(loanRepository.save(any(Loan.class))).thenReturn(saved);
    when(loanMapper.toDto(saved)).thenReturn(LoanDto.builder().id(10L).build());

    LoanDto result = loanService.create(input);

    assertEquals(10L, result.getId());
    verify(loanCache).invalidateByProfileId(1L);
    verify(loanCache).invalidateByCategory("Mortgage");
    verify(loanCache).invalidateByState("ACTIVE");
    verify(loanCache).clear();
  }

  @Test
  @DisplayName("create: profile == null в DTO — займ создаётся без профиля и без profile-инвалидации")
  void create_nullProfileInDto_skipsProfileInvalidation() {
    LoanDto input = LoanDto.builder()
        .amount(5000.0).currentState("ACTIVE")
        .profile(null).categories(null).build();
    Loan saved = Loan.builder().id(10L).currentState("ACTIVE")
        .profile(null).categories(new HashSet<>()).build();

    when(loanMapper.toEntity(input)).thenReturn(new Loan());
    when(loanRepository.save(any(Loan.class))).thenReturn(saved);
    when(loanMapper.toDto(saved)).thenReturn(LoanDto.builder().id(10L).build());

    loanService.create(input);

    verify(profileRepository, never()).findById(any());
    verify(loanCache, never()).invalidateByProfileId(any());
  }

  @Test
  @DisplayName("create: profile.id == null — поход в репозиторий не делается")
  void create_profileWithoutId_skipsLookup() {
    LoanDto input = LoanDto.builder()
        .amount(5000.0).currentState("ACTIVE")
        .profile(ProfileDto.builder().id(null).build())
        .categories(null).build();
    Loan saved = Loan.builder().id(10L).currentState("ACTIVE")
        .profile(null).categories(new HashSet<>()).build();

    when(loanMapper.toEntity(input)).thenReturn(new Loan());
    when(loanRepository.save(any(Loan.class))).thenReturn(saved);
    when(loanMapper.toDto(saved)).thenReturn(LoanDto.builder().id(10L).build());

    loanService.create(input);

    verify(profileRepository, never()).findById(any());
  }

  @Test
  @DisplayName("create: currentState == null у сохранённого займа — invalidateByState не вызывается")
  void create_nullState_skipsStateInvalidation() {
    LoanDto input = LoanDto.builder()
        .amount(5000.0).currentState(null)
        .profile(null).categories(null).build();
    Loan saved = Loan.builder().id(10L).currentState(null)
        .profile(null).categories(new HashSet<>()).build();

    when(loanMapper.toEntity(input)).thenReturn(new Loan());
    when(loanRepository.save(any(Loan.class))).thenReturn(saved);
    when(loanMapper.toDto(saved)).thenReturn(LoanDto.builder().id(10L).build());

    loanService.create(input);

    verify(loanCache, never()).invalidateByState(any());
  }

  @Test
  @DisplayName("create: profile не найден — NotFoundException")
  void create_profileNotFound_throws() {
    LoanDto input = buildDto(5000.0, "ACTIVE");
    when(profileRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> loanService.create(input));
    verify(loanRepository, never()).save(any());
  }

  @Test
  @DisplayName("create: category не найдена — NotFoundException")
  void create_categoryNotFound_throws() {
    LoanDto input = buildDto(5000.0, "ACTIVE");
    when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
    when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> loanService.create(input));
    verify(loanRepository, never()).save(any());
  }

  // =========================================================
  //                       update
  // =========================================================

  @Test
  @DisplayName("update: state меняется — старый и новый state инвалидируются")
  void update_stateChanged_invalidatesBoth() {
    Loan existing = buildLoan(5L, "ACTIVE");
    LoanDto input = LoanDto.builder().amount(7777.0).currentState("CLOSED").build();

    when(loanRepository.findById(5L)).thenReturn(Optional.of(existing));
    when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
    when(loanMapper.toDto(any(Loan.class))).thenReturn(LoanDto.builder().id(5L).build());

    loanService.update(5L, input);

    verify(loanCache).invalidateByProfileId(1L);
    verify(loanCache).invalidateByState("ACTIVE");
    verify(loanCache).invalidateByState("CLOSED");
    verify(loanCache, times(2)).invalidateByCategory("Mortgage");
  }

  @Test
  @DisplayName("update: oldState == newState — инвалидируется только новый (одним вызовом)")
  void update_sameState_invalidatesOnlyNew() {
    Loan existing = buildLoan(5L, "ACTIVE");
    LoanDto input = LoanDto.builder().amount(7777.0).currentState("ACTIVE").build();

    when(loanRepository.findById(5L)).thenReturn(Optional.of(existing));
    when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
    when(loanMapper.toDto(any(Loan.class))).thenReturn(LoanDto.builder().id(5L).build());

    loanService.update(5L, input);

    verify(loanCache, times(1)).invalidateByState("ACTIVE");
  }

  @Test
  @DisplayName("update: oldState == null, newState != null — invalidate один раз для нового")
  void update_oldStateNull_invalidatesNewOnly() {
    Loan existing = Loan.builder().id(5L).amount(1000.0).currentState(null)
        .profile(profile).categories(new HashSet<>(Set.of(category))).build();
    LoanDto input = LoanDto.builder().amount(7777.0).currentState("CLOSED").build();

    when(loanRepository.findById(5L)).thenReturn(Optional.of(existing));
    when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
    when(loanMapper.toDto(any(Loan.class))).thenReturn(LoanDto.builder().id(5L).build());

    loanService.update(5L, input);

    verify(loanCache, times(1)).invalidateByState("CLOSED");
  }

  @Test
  @DisplayName("update: oldState != null, newState == null — invalidate только старый (через первую ветку)")
  void update_newStateNull_invalidatesOldOnly() {
    Loan existing = buildLoan(5L, "ACTIVE");
    LoanDto input = LoanDto.builder().amount(7777.0).currentState(null).build();

    when(loanRepository.findById(5L)).thenReturn(Optional.of(existing));
    when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
    when(loanMapper.toDto(any(Loan.class))).thenReturn(LoanDto.builder().id(5L).build());

    loanService.update(5L, input);

    // oldState="ACTIVE", newState=null: oldState != null && !"ACTIVE".equals(null) -> true
    verify(loanCache).invalidateByState("ACTIVE");
    verify(loanCache).invalidateByState((String) null);
  }

  @Test
  @DisplayName("update: oldState == null && newState == null — invalidateByState вообще не вызывается")
  void update_bothStatesNull_noStateInvalidation() {
    Loan existing = Loan.builder().id(5L).amount(1000.0).currentState(null)
        .profile(profile).categories(new HashSet<>()).build();
    LoanDto input = LoanDto.builder().amount(7777.0).currentState(null).build();

    when(loanRepository.findById(5L)).thenReturn(Optional.of(existing));
    when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
    when(loanMapper.toDto(any(Loan.class))).thenReturn(LoanDto.builder().id(5L).build());

    loanService.update(5L, input);

    verify(loanCache, never()).invalidateByState(any());
  }

  @Test
  @DisplayName("update: profile == null у займа — invalidateByProfileId не вызывается")
  void update_loanWithoutProfile_skipsProfileInvalidation() {
    Loan existing = Loan.builder().id(5L).amount(1000.0).currentState("ACTIVE")
        .profile(null).categories(new HashSet<>()).build();
    LoanDto input = LoanDto.builder().amount(7777.0).currentState("CLOSED").build();

    when(loanRepository.findById(5L)).thenReturn(Optional.of(existing));
    when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
    when(loanMapper.toDto(any(Loan.class))).thenReturn(LoanDto.builder().id(5L).build());

    loanService.update(5L, input);

    verify(loanCache, never()).invalidateByProfileId(any());
  }

  @Test
  @DisplayName("update: займ не найден — NotFoundException")
  void update_notFound_throws() {
    when(loanRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> loanService.update(99L, LoanDto.builder().amount(1.0).currentState("X").build()));
    verify(loanRepository, never()).save(any());
  }

  // =========================================================
  //                       deleteById
  // =========================================================

  @Test
  @DisplayName("deleteById: всё заполнено — все инвалидации вызваны")
  void delete_fullLoan_invalidatesAll() {
    Loan existing = buildLoan(5L, "ACTIVE");
    when(loanRepository.findById(5L)).thenReturn(Optional.of(existing));

    loanService.deleteById(5L);

    verify(loanRepository).deleteById(5L);
    verify(loanCache).invalidateByProfileId(1L);
    verify(loanCache).invalidateByState("ACTIVE");
    verify(loanCache).invalidateByCategory("Mortgage");
    verify(loanCache).clear();
  }

  @Test
  @DisplayName("deleteById: profile==null и state==null — частичная инвалидация")
  void delete_loanWithNulls_partialInvalidation() {
    Loan existing = Loan.builder().id(5L).amount(1000.0).currentState(null)
        .profile(null).categories(new HashSet<>()).build();
    when(loanRepository.findById(5L)).thenReturn(Optional.of(existing));

    loanService.deleteById(5L);

    verify(loanRepository).deleteById(5L);
    verify(loanCache, never()).invalidateByProfileId(any());
    verify(loanCache, never()).invalidateByState(any());
    verify(loanCache).clear();
  }

  @Test
  @DisplayName("deleteById: займ не найден — NotFoundException")
  void delete_notFound_throws() {
    when(loanRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> loanService.deleteById(99L));
    verify(loanRepository, never()).deleteById(anyLong());
  }
}