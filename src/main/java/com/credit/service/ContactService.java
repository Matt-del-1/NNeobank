package com.credit.service;

import com.credit.dto.ContactDto;
import com.credit.mapper.ContactMapper;
import com.credit.model.Contact;
import com.credit.model.Profile;
import com.credit.repository.ContactRepository;
import com.credit.repository.ProfileRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactService {

  private final ContactRepository contactRepository;
  private final ProfileRepository profileRepository;
  private final ContactMapper contactMapper;

  @Transactional
  public ContactDto save(ContactDto dto) {
    Profile profile = profileRepository.findById(dto.getProfileId())
        .orElseThrow(() -> new RuntimeException("Profile not found"));

    Contact contact = contactMapper.toEntity(dto);
    contact.setProfile(profile);

    return contactMapper.toDto(contactRepository.save(contact));
  }

  @Transactional(readOnly = true)
  public ContactDto findById(Long id) {
    return contactRepository.findById(id)
        .map(contactMapper::toDto)
        .orElseThrow(() -> new RuntimeException("Contact not found"));
  }

  @Transactional(readOnly = true)
  public List<ContactDto> findAll() {
    return contactRepository.findAll().stream()
        .map(contactMapper::toDto)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<ContactDto> findByProfileId(Long profileId) {
    return contactRepository.findByProfileId(profileId).stream()
        .map(contactMapper::toDto)
        .toList();
  }

  @Transactional
  public ContactDto update(Long id, ContactDto dto) {
    Contact existingContact = contactRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Contact not found"));

    existingContact.setPhone(dto.getPhone());
    existingContact.setEmail(dto.getEmail());

    return contactMapper.toDto(contactRepository.save(existingContact));
  }

  @Transactional
  public void deleteById(Long id) {
    if (!contactRepository.existsById(id)) {
      throw new RuntimeException("Delete failed: Contact not found");
    }
    contactRepository.deleteById(id);
  }
}