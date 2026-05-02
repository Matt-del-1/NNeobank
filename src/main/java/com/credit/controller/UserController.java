package com.credit.controller;

import com.credit.dto.UserDto;
import com.credit.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
    return new ResponseEntity<>(userService.save(userDto), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<UserDto>> getAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(userService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDto> update(@PathVariable Long id,
      @Valid @RequestBody UserDto userDto) {
    return ResponseEntity.ok(userService.update(id, userDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}