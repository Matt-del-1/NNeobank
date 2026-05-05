package com.credit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.dto.UserDto;
import com.credit.exception.NotFoundException;
import com.credit.mapper.UserMapper;
import com.credit.model.User;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserService userService;

  // ---------------- save ----------------

  @Test
  @DisplayName("save: пользователь сохраняется и маппится в DTO")
  void save_returnsDto() {
    UserDto input = UserDto.builder().username("ivan").password("secret").build();
    User entity = User.builder().username("ivan").password("secret").build();
    User saved = User.builder().id(1L).username("ivan").password("secret").build();
    UserDto savedDto = UserDto.builder().id(1L).username("ivan").password("secret").build();

    when(userMapper.toEntity(input)).thenReturn(entity);
    when(userRepository.save(entity)).thenReturn(saved);
    when(userMapper.toDto(saved)).thenReturn(savedDto);

    UserDto result = userService.save(input);

    assertEquals(1L, result.getId());
    assertEquals("ivan", result.getUsername());
    verify(userRepository).save(entity);
  }

  // ---------------- findById ----------------

  @Test
  @DisplayName("findById: пользователь найден — возвращается DTO")
  void findById_found_returnsDto() {
    User entity = User.builder().id(1L).username("ivan").build();
    UserDto dto = UserDto.builder().id(1L).username("ivan").build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(userMapper.toDto(entity)).thenReturn(dto);

    UserDto result = userService.findById(1L);

    assertEquals("ivan", result.getUsername());
    verify(userRepository).findById(1L);
  }

  @Test
  @DisplayName("findById: пользователь не найден — NotFoundException")
  void findById_notFound_throws() {
    when(userRepository.findById(42L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.findById(42L));
    verify(userMapper, never()).toDto(any(User.class));
  }

  // ---------------- findAll ----------------

  @Test
  @DisplayName("findAll: возвращается список DTO")
  void findAll_returnsList() {
    User u1 = User.builder().id(1L).username("a").build();
    User u2 = User.builder().id(2L).username("b").build();
    when(userRepository.findAll()).thenReturn(List.of(u1, u2));
    when(userMapper.toDto(u1)).thenReturn(UserDto.builder().id(1L).username("a").build());
    when(userMapper.toDto(u2)).thenReturn(UserDto.builder().id(2L).username("b").build());

    List<UserDto> result = userService.findAll();

    assertEquals(2, result.size());
    assertEquals("a", result.get(0).getUsername());
    verify(userRepository).findAll();
  }

  @Test
  @DisplayName("findAll: пусто — возвращается пустой список")
  void findAll_empty() {
    when(userRepository.findAll()).thenReturn(List.of());

    List<UserDto> result = userService.findAll();

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  // ---------------- update ----------------

  @Test
  @DisplayName("update: данные пользователя обновляются (с новым паролем)")
  void update_withPassword_updatesAll() {
    User existing = User.builder().id(1L).username("old").password("oldpass").build();
    UserDto input = UserDto.builder().username("new").password("newpass").build();
    UserDto outDto = UserDto.builder().id(1L).username("new").password("newpass").build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    when(userMapper.toDto(any(User.class))).thenReturn(outDto);

    UserDto result = userService.update(1L, input);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertEquals("new", captor.getValue().getUsername());
    assertEquals("newpass", captor.getValue().getPassword());
    assertEquals("new", result.getUsername());
  }

  @Test
  @DisplayName("update: пустой пароль — пароль НЕ перезаписывается")
  void update_withEmptyPassword_keepsOldPassword() {
    User existing = User.builder().id(1L).username("old").password("oldpass").build();
    UserDto input = UserDto.builder().username("new").password("").build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    when(userMapper.toDto(any(User.class))).thenReturn(new UserDto());

    userService.update(1L, input);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertEquals("oldpass", captor.getValue().getPassword(),
        "Пароль не должен меняться, если в DTO пустая строка");
    assertEquals("new", captor.getValue().getUsername());
  }

  @Test
  @DisplayName("update: password == null — пароль НЕ перезаписывается")
  void update_withNullPassword_keepsOldPassword() {
    User existing = User.builder().id(1L).username("old").password("oldpass").build();
    UserDto input = UserDto.builder().username("new").password(null).build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
    when(userMapper.toDto(any(User.class))).thenReturn(new UserDto());

    userService.update(1L, input);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertEquals("oldpass", captor.getValue().getPassword(),
        "Пароль не должен меняться, если в DTO null");
  }

  @Test
  @DisplayName("update: пользователь не найден — NotFoundException")
  void update_notFound_throws() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> userService.update(99L, UserDto.builder().username("x").build()));
    verify(userRepository, never()).save(any());
  }

  // ---------------- deleteById ----------------

  @Test
  @DisplayName("deleteById: вызывает репозиторий")
  void deleteById_callsRepo() {
    userService.deleteById(1L);
    verify(userRepository, times(1)).deleteById(1L);
  }
}