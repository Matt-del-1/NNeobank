package com.credit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {

  private Long id;

  @NotNull(message = "userId обязателен")
  private Long userId;

  @NotBlank(message = "firstName не может быть пустым")
  @Size(max = 50, message = "firstName не должен превышать 50 символов")
  private String firstName;

  @NotBlank(message = "lastName не может быть пустым")
  @Size(max = 50, message = "lastName не должен превышать 50 символов")
  private String lastName;

  @Size(max = 50, message = "middleName не должен превышать 50 символов")
  private String middleName;
}