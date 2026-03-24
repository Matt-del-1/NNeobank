package com.credit.mapper;

import com.credit.dto.ContactDto;
import com.credit.model.Contact;
import com.credit.model.Profile;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

  public ContactDto toDto(Contact contact) {
    if (contact == null) {
      return null;
    }
    return ContactDto.builder()
        .id(contact.getId())
        .profileId(contact.getProfile() != null ? contact.getProfile().getId() : null)
        .phone(contact.getPhone())
        .email(contact.getEmail())
        .build();
  }

  public Contact toEntity(ContactDto dto) {
    if (dto == null) {
      return null;
    }
    Profile profile = null;
    return Contact.builder()
        .id(dto.getId())
        .profile(null)
        .phone(dto.getPhone())
        .email(dto.getEmail())
        .build();
  }
}