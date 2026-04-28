package com.credit.dto;

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
  private Long profileId; // Ссылка на владельца контактов
  private String phone;
  private String email;
}
