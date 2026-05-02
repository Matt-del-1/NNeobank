package com.credit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

  private Long id;

  @NotBlank(message = "Username не может быть пустым")
  @Size(min = 3, max = 50, message = "Username должен быть длиной от 3 до 50 символов")
  private String username;

  @NotBlank(message = "Password не может быть пустым")
  @Size(min = 6, max = 100, message = "Password должен быть длиной от 6 до 100 символов")
  private String password;
}