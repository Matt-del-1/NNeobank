package com.credit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.dto.ContactDto;
import com.credit.exception.NotFoundException;
import com.credit.mapper.ContactMapper;
import com.credit.model.Contact;
import com.credit.model.Profile;
import com.credit.repository.ContactRepository;
import com.credit.repository.ProfileRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

  @Mock
  private ContactRepository contactRepository;
  @Mock
  private ProfileRepository profileRepository;
  @Mock
  private ContactMapper contactMapper;

  @InjectMocks
  private ContactService contactService;

  // ---------------- helpers ----------------

  private Profile profile() {
    return Profile.builder().id(1L).firstName("Ivan").lastName("Ivanov").build();
  }

  private ContactDto inputDto() {
    return ContactDto.builder()
        .profileId(1L)
        .phone("+7-900-000-00-00")
        .email("a@a.ru")
        .build();
  }

  // ---------------- create / save ----------------

  @Test
  @DisplayName("create: профиль найден — контакт сохраняется с проставленным Profile")
  void create_profileFound_savesWithProfile() {
    ContactDto input = inputDto();
    Contact entity = Contact.builder().phone(input.getPhone()).email(input.getEmail()).build();
    Contact saved = Contact.builder()
        .id(5L).phone(input.getPhone()).email(input.getEmail()).profile(profile()).build();

    when(profileRepository.findById(1L)).thenReturn(Optional.of(profile()));
    when(contactMapper.toEntity(input)).thenReturn(entity);
    when(contactRepository.save(any(Contact.class))).thenReturn(saved);
    when(contactMapper.toDto(saved))
        .thenReturn(ContactDto.builder().id(5L).profileId(1L).email("a@a.ru").build());

    ContactDto result = contactService.create(input);

    assertEquals(5L, result.getId());

    ArgumentCaptor<Contact> captor = ArgumentCaptor.forClass(Contact.class);
    verify(contactRepository).save(captor.capture());
    assertEquals(1L, captor.getValue().getProfile().getId(),
        "Перед сохранением сервис должен подставить найденный Profile");
  }

  @Test
  @DisplayName("create: профиль не найден — NotFoundException, save не вызван")
  void create_profileNotFound_throws() {
    when(profileRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> contactService.create(inputDto()));
    verify(contactRepository, never()).save(any());
  }

  // ---------------- findById ----------------

  @Test
  @DisplayName("findById: контакт найден — возвращается DTO")
  void findById_found_returnsDto() {
    Contact entity = Contact.builder().id(5L).email("a@a.ru").build();
    when(contactRepository.findById(5L)).thenReturn(Optional.of(entity));
    when(contactMapper.toDto(entity))
        .thenReturn(ContactDto.builder().id(5L).email("a@a.ru").build());

    ContactDto result = contactService.findById(5L);

    assertEquals("a@a.ru", result.getEmail());
  }

  @Test
  @DisplayName("findById: контакт не найден — NotFoundException")
  void findById_notFound_throws() {
    when(contactRepository.findById(5L)).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> contactService.findById(5L));
  }

  // ---------------- findAll ----------------

  @Test
  @DisplayName("findAll: возвращается список")
  void findAll_returnsList() {
    Contact c1 = Contact.builder().id(1L).build();
    Contact c2 = Contact.builder().id(2L).build();
    when(contactRepository.findAll()).thenReturn(List.of(c1, c2));
    when(contactMapper.toDto(c1)).thenReturn(ContactDto.builder().id(1L).build());
    when(contactMapper.toDto(c2)).thenReturn(ContactDto.builder().id(2L).build());

    assertEquals(2, contactService.findAll().size());
  }

  // ---------------- findByProfileId ----------------

  @Test
  @DisplayName("findByProfileId: возвращается список контактов профиля")
  void findByProfileId_returnsList() {
    Contact c1 = Contact.builder().id(1L).build();
    when(contactRepository.findByProfileId(1L)).thenReturn(List.of(c1));
    when(contactMapper.toDto(c1)).thenReturn(ContactDto.builder().id(1L).profileId(1L).build());

    List<ContactDto> result = contactService.findByProfileId(1L);

    assertEquals(1, result.size());
    verify(contactRepository).findByProfileId(1L);
  }

  // ---------------- update ----------------

  @Test
  @DisplayName("update: phone и email обновляются")
  void update_updatesPhoneAndEmail() {
    Contact existing = Contact.builder().id(5L).phone("old").email("old@a.ru").build();
    ContactDto input = ContactDto.builder()
        .profileId(1L).phone("+7-999").email("new@a.ru").build();

    when(contactRepository.findById(5L)).thenReturn(Optional.of(existing));
    when(contactRepository.save(any(Contact.class))).thenAnswer(inv -> inv.getArgument(0));
    when(contactMapper.toDto(any(Contact.class)))
        .thenReturn(ContactDto.builder().id(5L).email("new@a.ru").phone("+7-999").build());

    ContactDto result = contactService.update(5L, input);

    ArgumentCaptor<Contact> captor = ArgumentCaptor.forClass(Contact.class);
    verify(contactRepository).save(captor.capture());
    assertEquals("+7-999", captor.getValue().getPhone());
    assertEquals("new@a.ru", captor.getValue().getEmail());
    assertEquals("new@a.ru", result.getEmail());
  }

  @Test
  @DisplayName("update: контакт не найден — NotFoundException")
  void update_notFound_throws() {
    when(contactRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> contactService.update(99L, ContactDto.builder().profileId(1L).build()));
    verify(contactRepository, never()).save(any());
  }

  // ---------------- deleteById ----------------

  @Test
  @DisplayName("deleteById: вызывает репозиторий")
  void deleteById_callsRepo() {
    contactService.deleteById(5L);
    verify(contactRepository, times(1)).deleteById(5L);
  }
}