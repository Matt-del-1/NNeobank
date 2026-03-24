package com.credit.dto;

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
  private Double amount;
  private String currentState;
  private LocalDateTime lastUpdate;
  private Long profileId;
  private Set<Long> categoryIds;
}