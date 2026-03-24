package com.credit.mapper;

import com.credit.dto.UserDto;
import com.credit.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserDto toDto(User user) {
    if (user == null) {
      return null;
    }
    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .password(user.getPassword())
        .build();
  }

  public User toEntity(UserDto dto) {
    if (dto == null) {
      return null;
    }
    return User.builder()
        .id(dto.getId())
        .username(dto.getUsername())
        .password(dto.getPassword())
        .build();
  }
}