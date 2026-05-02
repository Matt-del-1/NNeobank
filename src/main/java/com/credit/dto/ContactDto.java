package com.credit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactDto {

  private Long id;

  @NotNull(message = "profileId обязателен")
  private Long profileId;

  @Pattern(
      regexp = "^\\+?[0-9\\-\\s()]{5,20}$",
      message = "Некорректный формат телефона"
  )
  private String phone;

  @Email(message = "Некорректный формат email")
  @Size(max = 100, message = "email не должен превышать 100 символов")
  private String email;
}