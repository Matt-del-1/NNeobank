package com.credit.service;

import com.credit.dto.ProfileDto;
import com.credit.mapper.ProfileMapper;
import com.credit.model.Profile;
import com.credit.model.User;
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
    existingProfile.setMiddleName(dto.getLast2Name());

    return profileMapper.toDto(profileRepository.save(existingProfile));
  }

  @Transactional
  public void deleteById(Long id) {
    profileRepository.deleteById(id);
  }
}
