package com.credit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

  private Long id;

  @NotBlank(message = "Название категории не может быть пустым")
  @Size(max = 100, message = "Название категории не должно превышать 100 символов")
  private String name;

  @NotNull(message = "Ставка обязательна")
  @PositiveOrZero(message = "Ставка не может быть отрицательной")
  private Float rate;
}