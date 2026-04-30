package com.credit.mapper;

import com.credit.dto.LoanDto;
import com.credit.model.Category;
import com.credit.model.Loan;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanMapper {

  private final ProfileMapper profileMapper;
  private final CategoryMapper categoryMapper;

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
      dto.setProfile(profileMapper.toDto(entity.getProfile()));
    }

    Set<com.credit.dto.CategoryDto> categories = new HashSet<>();
    if (entity.getCategories() != null) {
      for (Category category : entity.getCategories()) {
        categories.add(categoryMapper.toDto(category));
      }
    }
    dto.setCategories(categories);

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
