package com.credit.dto;

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
  private Long userId;
  private String firstName;
  private String lastName;
  private String middleName;
}
