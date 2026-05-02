package com.credit.controller;

import com.credit.dto.ProfileDto;
import com.credit.service.ProfileService;
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
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;

  @PostMapping
  public ResponseEntity<ProfileDto> create(@Valid @RequestBody ProfileDto profileDto) {
    return new ResponseEntity<>(profileService.create(profileDto), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<ProfileDto>> getAll() {
    return ResponseEntity.ok(profileService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProfileDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(profileService.findById(id));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<ProfileDto> getByUserId(@PathVariable Long userId) {
    return ResponseEntity.ok(profileService.findByUserId(userId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProfileDto> update(@PathVariable Long id,
      @Valid @RequestBody ProfileDto profileDto) {
    return ResponseEntity.ok(profileService.update(id, profileDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    profileService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/test-transaction")
  public ResponseEntity<String> testTransaction(@Valid @RequestBody TransactionTestRequest request) {
    profileService.saveFullProfileDemo(request.getProfile(), request.getContacts());
    return ResponseEntity.ok("Всё сохранено успешно (транзакция завершена)");
  }

  @lombok.Data
  public static class TransactionTestRequest {

    @jakarta.validation.Valid
    @jakarta.validation.constraints.NotNull(message = "profile обязателен")
    private com.credit.dto.ProfileDto profile;

    @jakarta.validation.Valid
    private java.util.List<com.credit.dto.ContactDto> contacts;
  }
}