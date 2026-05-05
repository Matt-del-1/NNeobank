package com.credit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.credit.dto.CategoryDto;
import com.credit.exception.BusinessException;
import com.credit.exception.NotFoundException;
import com.credit.mapper.CategoryMapper;
import com.credit.model.Category;
import com.credit.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private CategoryMapper categoryMapper;

  @InjectMocks
  private CategoryService categoryService;

  // ---------------- create ----------------

  @Test
  @DisplayName("create: сущность сохраняется, возвращается DTO")
  void create_savesAndReturnsDto() {
    CategoryDto inputDto = CategoryDto.builder().name("Auto").rate(5.0f).build();
    Category entity = Category.builder().name("Auto").rate(5.0f).build();
    Category saved = Category.builder().id(10L).name("Auto").rate(5.0f).build();
    CategoryDto savedDto = CategoryDto.builder().id(10L).name("Auto").rate(5.0f).build();

    when(categoryMapper.toEntity(inputDto)).thenReturn(entity);
    when(categoryRepository.save(entity)).thenReturn(saved);
    when(categoryMapper.toDto(saved)).thenReturn(savedDto);

    CategoryDto result = categoryService.create(inputDto);

    assertEquals(10L, result.getId());
    verify(categoryRepository).save(any(Category.class));
  }

  // ---------------- findById ----------------

  @Test
  @DisplayName("findById: категория есть — возвращается DTO")
  void findById_found_returnsDto() {
    Category entity = Category.builder().id(1L).name("Mortgage").rate(7.5f).build();
    CategoryDto dto = CategoryDto.builder().id(1L).name("Mortgage").rate(7.5f).build();

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(categoryMapper.toDto(entity)).thenReturn(dto);

    CategoryDto result = categoryService.findById(1L);

    assertEquals("Mortgage", result.getName());
    verify(categoryRepository).findById(1L);
  }

  @Test
  @DisplayName("findById: категории нет — NotFoundException")
  void findById_notFound_throws() {
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> categoryService.findById(99L));
  }

  // ---------------- findAll ----------------

  @Test
  @DisplayName("findAll: возвращается список DTO")
  void findAll_returnsList() {
    Category c1 = Category.builder().id(1L).name("A").build();
    Category c2 = Category.builder().id(2L).name("B").build();

    when(categoryRepository.findAll()).thenReturn(List.of(c1, c2));
    when(categoryMapper.toDto(c1)).thenReturn(CategoryDto.builder().id(1L).name("A").build());
    when(categoryMapper.toDto(c2)).thenReturn(CategoryDto.builder().id(2L).name("B").build());

    List<CategoryDto> result = categoryService.findAll();

    assertEquals(2, result.size());
  }

  // ---------------- update ----------------

  @Test
  @DisplayName("update: поля сущности обновляются и сохраняются")
  void update_updatesFields() {
    Category existing = Category.builder().id(1L).name("OldName").rate(1.0f).build();
    CategoryDto input = CategoryDto.builder().name("NewName").rate(9.5f).build();

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));
    when(categoryMapper.toDto(any(Category.class)))
        .thenReturn(CategoryDto.builder().id(1L).name("NewName").rate(9.5f).build());

    CategoryDto result = categoryService.update(1L, input);

    ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
    verify(categoryRepository).save(captor.capture());
    assertEquals("NewName", captor.getValue().getName());
    assertEquals(9.5f, captor.getValue().getRate());
    assertEquals("NewName", result.getName());
  }

  @Test
  @DisplayName("update: категория не найдена — NotFoundException")
  void update_notFound_throws() {
    when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> categoryService.update(99L, CategoryDto.builder().name("x").rate(1f).build()));
    verify(categoryRepository, never()).save(any());
  }

  // ---------------- deleteById ----------------

  @Test
  @DisplayName("deleteById: вызывает репозиторий")
  void deleteById_callsRepo() {
    categoryService.deleteById(1L);
    verify(categoryRepository, times(1)).deleteById(1L);
  }

  // ---------------- doubleSaveDemo ----------------

  @Test
  @DisplayName("doubleSaveDemo: все DTO валидны — все сохраняются")
  void doubleSaveDemo_allValid_savesAll() {
    List<CategoryDto> input = List.of(
        CategoryDto.builder().name("A").rate(1f).build(),
        CategoryDto.builder().name("B").rate(2f).build()
    );

    when(categoryMapper.toEntity(any(CategoryDto.class))).thenReturn(new Category());

    categoryService.doubleSaveDemo(input);

    verify(categoryRepository, times(2)).save(any(Category.class));
  }

  @Test
  @DisplayName("doubleSaveDemo: отрицательная ставка во втором — BusinessException, первый уже сохранён")
  void doubleSaveDemo_negativeRate_throws() {
    List<CategoryDto> input = List.of(
        CategoryDto.builder().name("A").rate(1f).build(),
        CategoryDto.builder().name("Bad").rate(-5f).build(),
        CategoryDto.builder().name("C").rate(3f).build()
    );

    when(categoryMapper.toEntity(any(CategoryDto.class))).thenReturn(new Category());

    assertThrows(BusinessException.class, () -> categoryService.doubleSaveDemo(input));
    verify(categoryRepository, times(1)).save(any(Category.class));
  }

  @Test
  @DisplayName("doubleSaveDemo: rate == null — не падает, сохраняется как обычно")
  void doubleSaveDemo_nullRate_savesNormally() {
    List<CategoryDto> input = List.of(
        CategoryDto.builder().name("NoRate").rate(null).build()
    );
    when(categoryMapper.toEntity(any(CategoryDto.class))).thenReturn(new Category());

    categoryService.doubleSaveDemo(input);

    verify(categoryRepository, times(1)).save(any(Category.class));
  }
}