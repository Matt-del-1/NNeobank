package com.credit.service;

import com.credit.dto.ContactDto;
import com.credit.dto.ProfileDto;
import com.credit.mapper.ContactMapper;
import com.credit.mapper.ProfileMapper;
import com.credit.model.Contact;
import com.credit.model.Profile;
import com.credit.model.User;
import com.credit.repository.ContactRepository;
import com.credit.repository.ProfileRepository;
import com.credit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final UserRepository userRepository;
  private final ProfileMapper profileMapper;
  private final ContactRepository contactRepository;
  private final ContactMapper contactMapper;

  @Transactional
  public ProfileDto create(ProfileDto dto) {
    User user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

    Profile profile = profileMapper.toEntity(dto);
    profile.setUser(user);

    return profileMapper.toDto(profileRepository.save(profile));
  }

  @Transactional(readOnly = true)
  public ProfileDto findById(Long id) {
    return profileRepository.findById(id)
        .map(profileMapper::toDto)
        .orElseThrow(() -> new RuntimeException("Profile not found with ID: " + id));
  }

  @Transactional(readOnly = true)
  public ProfileDto findByUserId(Long userId) {
    return profileRepository.findByUserId(userId)
        .map(profileMapper::toDto)
        .orElseThrow(() -> new RuntimeException("Profile not found for userId: " + userId));
  }

  @Transactional(readOnly = true)
  public List<ProfileDto> findAll() {
    return profileRepository.findAll().stream()
        .map(profileMapper::toDto)
        .toList();
  }

  @Transactional
  public ProfileDto update(Long id, ProfileDto dto) {
    Profile existingProfile = profileRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Profile not found"));

    existingProfile.setFirstName(dto.getFirstName());
    existingProfile.setLastName(dto.getLastName());
    existingProfile.setMiddleName(dto.getMiddleName());

    return profileMapper.toDto(profileRepository.save(existingProfile));
  }

  @Transactional
  public void deleteById(Long id) {
    profileRepository.deleteById(id);
  }

  @Transactional
  public void saveFullProfileDemo(ProfileDto profileDto, List<ContactDto> contactDtos) {
    User user = userRepository.findById(profileDto.getUserId())
        .orElseThrow(() -> new RuntimeException(
            "Ошибка: Сначала создайте User с ID " + profileDto.getUserId()));

    Profile profile = profileMapper.toEntity(profileDto);
    profile.setUser(user);
    Profile savedProfile = profileRepository.save(profile);
    System.out.println(">>> Профиль сохранен. ID: " + savedProfile.getId());

    for (ContactDto contactDto : contactDtos) {
      if (contactDto.getEmail() != null && contactDto.getEmail().contains("error")) {
        System.out.println("Error, rollback...");
        throw new RuntimeException("error to rollback");
      }

      Contact contact = contactMapper.toEntity(contactDto);
      contact.setProfile(savedProfile);
      contactRepository.save(contact);
      System.out.println(">>> Contact saved: " + contact.getEmail());
    }
  }
}
