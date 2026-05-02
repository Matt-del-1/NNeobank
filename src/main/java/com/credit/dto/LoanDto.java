package com.credit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDto {

  private Long id;

  @NotNull(message = "amount обязателен")
  @Positive(message = "amount должен быть положительным")
  private Double amount;

  @NotBlank(message = "currentState не может быть пустым")
  private String currentState;

  private LocalDateTime lastUpdate;

  private ProfileDto profile;

  private Set<CategoryDto> categories;
}