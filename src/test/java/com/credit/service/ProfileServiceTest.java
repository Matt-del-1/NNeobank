package com.credit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.dto.ContactDto;
import com.credit.dto.ProfileDto;
import com.credit.exception.BusinessException;
import com.credit.exception.NotFoundException;
import com.credit.mapper.ContactMapper;
import com.credit.mapper.ProfileMapper;
import com.credit.model.Contact;
import com.credit.model.Profile;
import com.credit.model.User;
import com.credit.repository.ContactRepository;
import com.credit.repository.ProfileRepository;
import com.credit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileServiceTest {

  @Mock
  private ProfileRepository profileRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ProfileMapper profileMapper;
  @Mock
  private ContactRepository contactRepository;
  @Mock
  private ContactMapper contactMapper;

  @InjectMocks
  private ProfileService profileService;

  // ---------------- helpers ----------------

  private User user() {
    return User.builder().id(1L).username("ivan").build();
  }

  private ProfileDto profileDto() {
    return ProfileDto.builder()
        .userId(1L).firstName("Ivan").lastName("Ivanov").middleName("I.").build();
  }

  // ---------------- create ----------------

  @Test
  @DisplayName("create: user найден — профиль сохраняется")
  void create_userFound_saves() {
    ProfileDto input = profileDto();
    Profile entity = Profile.builder().firstName("Ivan").lastName("Ivanov").build();
    Profile saved = Profile.builder().id(10L).firstName("Ivan").lastName("Ivanov")
        .user(user()).build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
    when(profileMapper.toEntity(input)).thenReturn(entity);
    when(profileRepository.save(any(Profile.class))).thenReturn(saved);
    when(profileMapper.toDto(saved))
        .thenReturn(ProfileDto.builder().id(10L).userId(1L).firstName("Ivan").build());

    ProfileDto result = profileService.create(input);

    assertEquals(10L, result.getId());
    ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);
    verify(profileRepository).save(captor.capture());
    assertEquals(1L, captor.getValue().getUser().getId(),
        "Перед сохранением сервис должен подставить найденный User");
  }

  @Test
  @DisplayName("create: user не найден — NotFoundException, save не вызван")
  void create_userNotFound_throws() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> profileService.create(profileDto()));
    verify(profileRepository, never()).save(any());
  }

  // ---------------- findById ----------------

  @Test
  @DisplayName("findById: профиль найден — возвращается DTO")
  void findById_found_returnsDto() {
    Profile entity = Profile.builder().id(10L).firstName("Ivan").build();
    when(profileRepository.findById(10L)).thenReturn(Optional.of(entity));
    when(profileMapper.toDto(entity))
        .thenReturn(ProfileDto.builder().id(10L).firstName("Ivan").build());

    assertEquals("Ivan", profileService.findById(10L).getFirstName());
  }

  @Test
  @DisplayName("findById: не найден — NotFoundException")
  void findById_notFound_throws() {
    when(profileRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> profileService.findById(99L));
  }

  // ---------------- findByUserId ----------------

  @Test
  @DisplayName("findByUserId: найден — DTO")
  void findByUserId_found() {
    Profile entity = Profile.builder().id(10L).firstName("Ivan").build();
    when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(entity));
    when(profileMapper.toDto(entity))
        .thenReturn(ProfileDto.builder().id(10L).firstName("Ivan").build());

    assertEquals(10L, profileService.findByUserId(1L).getId());
  }

  @Test
  @DisplayName("findByUserId: не найден — NotFoundException")
  void findByUserId_notFound_throws() {
    when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> profileService.findByUserId(1L));
  }

  // ---------------- findAll ----------------

  @Test
  @DisplayName("findAll: возвращает список DTO")
  void findAll_returnsList() {
    Profile p1 = Profile.builder().id(1L).build();
    Profile p2 = Profile.builder().id(2L).build();
    when(profileRepository.findAll()).thenReturn(List.of(p1, p2));
    when(profileMapper.toDto(p1)).thenReturn(ProfileDto.builder().id(1L).build());
    when(profileMapper.toDto(p2)).thenReturn(ProfileDto.builder().id(2L).build());

    assertEquals(2, profileService.findAll().size());
  }

  // ---------------- update ----------------

  @Test
  @DisplayName("update: имена обновляются")
  void update_updatesNames() {
    Profile existing = Profile.builder().id(10L).firstName("Old").lastName("Olds").build();
    ProfileDto input = ProfileDto.builder()
        .userId(1L).firstName("New").lastName("News").middleName("M.").build();

    when(profileRepository.findById(10L)).thenReturn(Optional.of(existing));
    when(profileRepository.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));
    when(profileMapper.toDto(any(Profile.class)))
        .thenReturn(ProfileDto.builder().id(10L).firstName("New").lastName("News").build());

    ProfileDto result = profileService.update(10L, input);

    ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);
    verify(profileRepository).save(captor.capture());
    assertEquals("New", captor.getValue().getFirstName());
    assertEquals("News", captor.getValue().getLastName());
    assertEquals("M.", captor.getValue().getMiddleName());
    assertEquals("New", result.getFirstName());
  }

  @Test
  @DisplayName("update: профиль не найден — NotFoundException")
  void update_notFound_throws() {
    when(profileRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class,
        () -> profileService.update(99L, profileDto()));
    verify(profileRepository, never()).save(any());
  }

  // ---------------- deleteById ----------------

  @Test
  @DisplayName("deleteById: вызывает репозиторий")
  void deleteById_callsRepo() {
    profileService.deleteById(10L);
    verify(profileRepository, times(1)).deleteById(10L);
  }

  // =========================================================
  //              saveFullProfileDemo (транзакция)
  // =========================================================

  @Test
  @DisplayName("saveFullProfileDemo: все контакты валидны — профиль и контакты сохранены")
  void saveFullProfileDemo_allValid_savesAll() {
    ProfileDto pDto = profileDto();
    List<ContactDto> contacts = List.of(
        ContactDto.builder().profileId(1L).email("a@a.ru").phone("+7-1").build(),
        ContactDto.builder().profileId(1L).email("b@a.ru").phone("+7-2").build()
    );

    Profile entity = Profile.builder().firstName("Ivan").build();
    Profile saved = Profile.builder().id(10L).firstName("Ivan").user(user()).build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
    when(profileMapper.toEntity(pDto)).thenReturn(entity);
    when(profileRepository.save(any(Profile.class))).thenReturn(saved);
    when(contactMapper.toEntity(any(ContactDto.class)))
        .thenAnswer(inv -> {
          ContactDto d = inv.getArgument(0);
          return Contact.builder().email(d.getEmail()).phone(d.getPhone()).build();
        });

    profileService.saveFullProfileDemo(pDto, contacts);

    verify(profileRepository, times(1)).save(any(Profile.class));
    verify(contactRepository, times(2)).save(any(Contact.class));
  }

  @Test
  @DisplayName("saveFullProfileDemo: user не найден — NotFoundException, ничего не сохранено")
  void saveFullProfileDemo_userNotFound_throws() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> profileService.saveFullProfileDemo(profileDto(), List.of()));

    verify(profileRepository, never()).save(any());
    verify(contactRepository, never()).save(any());
  }

  @Test
  @DisplayName("saveFullProfileDemo: email с 'error' во втором контакте — BusinessException, "
      + "профиль и первый контакт уже сохранены")
  void saveFullProfileDemo_errorEmail_throwsAfterPartialSave() {
    ProfileDto pDto = profileDto();
    List<ContactDto> contacts = List.of(
        ContactDto.builder().profileId(1L).email("ok@a.ru").phone("+7-1").build(),
        ContactDto.builder().profileId(1L).email("error@a.ru").phone("+7-2").build(),
        ContactDto.builder().profileId(1L).email("c@a.ru").phone("+7-3").build()
    );

    Profile entity = Profile.builder().firstName("Ivan").build();
    Profile saved = Profile.builder().id(10L).firstName("Ivan").user(user()).build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
    when(profileMapper.toEntity(pDto)).thenReturn(entity);
    when(profileRepository.save(any(Profile.class))).thenReturn(saved);
    when(contactMapper.toEntity(any(ContactDto.class)))
        .thenAnswer(inv -> {
          ContactDto d = inv.getArgument(0);
          return Contact.builder().email(d.getEmail()).phone(d.getPhone()).build();
        });

    assertThrows(BusinessException.class,
        () -> profileService.saveFullProfileDemo(pDto, contacts));

    verify(profileRepository, times(1)).save(any(Profile.class));
    verify(contactRepository, times(1)).save(any(Contact.class));
  }

  @Test
  @DisplayName("saveFullProfileDemo: контакт с email==null — пропускается без ошибки")
  void saveFullProfileDemo_nullEmail_savesNormally() {
    ProfileDto pDto = profileDto();
    List<ContactDto> contacts = List.of(
        ContactDto.builder().profileId(1L).email(null).phone("+7-1").build()
    );

    Profile entity = Profile.builder().firstName("Ivan").build();
    Profile saved = Profile.builder().id(10L).firstName("Ivan").user(user()).build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
    when(profileMapper.toEntity(pDto)).thenReturn(entity);
    when(profileRepository.save(any(Profile.class))).thenReturn(saved);
    when(contactMapper.toEntity(any(ContactDto.class)))
        .thenReturn(Contact.builder().phone("+7-1").build());

    profileService.saveFullProfileDemo(pDto, contacts);

    verify(contactRepository, times(1)).save(any(Contact.class));
  }
}