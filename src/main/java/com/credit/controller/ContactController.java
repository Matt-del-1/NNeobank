package com.credit.controller;

import com.credit.dto.ContactDto;
import com.credit.service.ContactService;
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
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

  private final ContactService contactService;

  @PostMapping
  public ResponseEntity<ContactDto> create(@RequestBody ContactDto contactDto) {
    return new ResponseEntity<>(contactService.save(contactDto), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<ContactDto>> getAll() {
    return ResponseEntity.ok(contactService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ContactDto> getById(@PathVariable Long id) {
    return ResponseEntity.ok(contactService.findById(id));
  }

  @GetMapping("/profile/{profileId}")
  public ResponseEntity<List<ContactDto>> getByProfile(@PathVariable Long profileId) {
    return ResponseEntity.ok(contactService.findByProfileId(profileId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ContactDto> update(@PathVariable Long id,
      @RequestBody ContactDto contactDto) {
    return ResponseEntity.ok(contactService.update(id, contactDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    contactService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}