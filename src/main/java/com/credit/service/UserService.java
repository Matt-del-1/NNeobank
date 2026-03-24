package com.credit.service;

import com.credit.dto.UserDto;
import com.credit.mapper.UserMapper;
import com.credit.model.User;
import com.credit.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  public UserDto save(UserDto dto) {
    if (userRepository.existsByUsername(dto.getUsername())) {
      throw new RuntimeException("Username is already taken");
    }
    User user = userMapper.toEntity(dto);
    return userMapper.toDto(userRepository.save(user));
  }

  @Transactional(readOnly = true)
  public UserDto findById(Long id) {
    return userRepository.findById(id)
        .map(userMapper::toDto)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Transactional
  public UserDto update(Long id, UserDto dto) {
    User existingUser = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("User not found"));

    existingUser.setUsername(dto.getUsername());
    if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
      existingUser.setPassword(dto.getPassword());
    }

    return userMapper.toDto(userRepository.save(existingUser));
  }

  @Transactional
  public void deleteById(Long id) {
    if (!userRepository.existsById(id)) {
      throw new RuntimeException("User not found");
    }
    userRepository.deleteById(id);
  }
}