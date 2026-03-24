package com.credit.mapper;

import com.credit.dto.LoanDto;
import com.credit.model.Category;
import com.credit.model.Loan;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {

  public LoanDto toDto(Loan entity) {
    if (entity == null) {
      return null;
    }

    LoanDto dto = new LoanDto();
    dto.setId(entity.getId());
    dto.setAmount(entity.getAmount());
    dto.setCurrentState(entity.getCurrentState());
    dto.setLastUpdate(entity.getLastUpdate());

    if (entity.getProfile() != null) {
      dto.setProfileId(entity.getProfile().getId());
    }

    Set<Long> categoryIds = new HashSet<>();
    if (entity.getCategories() != null) {
      for (Category category : entity.getCategories()) {
        categoryIds.add(category.getId());
      }
    }
    dto.setCategoryIds(categoryIds);

    return dto;
  }

  public Loan toEntity(LoanDto dto) {
    if (dto == null) {
      return null;
    }

    Loan entity = new Loan();
    entity.setAmount(dto.getAmount());
    entity.setCurrentState(dto.getCurrentState());

    return entity;
  }
}