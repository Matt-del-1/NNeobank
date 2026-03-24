package com.credit.mapper;

import com.credit.dto.ProfileDto;
import com.credit.model.Profile;
import com.credit.model.User;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

  public ProfileDto toDto(Profile profile) {
    if (profile == null) {
      return null;
    }
    return ProfileDto.builder()
        .id(profile.getId())
        .userId(profile.getUser() != null ? profile.getUser().getId() : null)
        .firstName(profile.getFirstName())
        .lastName(profile.getLastName())
        .last2Name(profile.getMiddleName())
        .build();
  }

  public Profile toEntity(ProfileDto dto) {
    if (dto == null) {
      return null;
    }
    return Profile.builder()
        .id(dto.getId())
        .user(null)
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .middleName(dto.getLast2Name())
        .build();
  }
}