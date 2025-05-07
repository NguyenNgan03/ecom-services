package com.example.bookstore.service;

import com.example.bookstore.dto.request.CategoryDTO;
import com.example.bookstore.dto.response.CategoryResponseDTO;
import com.example.bookstore.entity.Category;
import com.example.bookstore.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Sử dụng MockitoExtension để khởi tạo mock
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryDTO categoryDTO;
    private CategoryResponseDTO categoryResponseDTO;

    @BeforeEach
    void setUp() {
        // Không cần gọi MockitoAnnotations.openMocks(this) vì đã dùng @ExtendWith(MockitoExtension.class)

        // Initialize test data
        category = new Category();
        category.setId(1);
        category.setName("Fiction");
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setIsDeleted(false);

        categoryDTO = new CategoryDTO();
        categoryDTO.setName("Fiction");

        categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(1);
        categoryResponseDTO.setName("Fiction");
        categoryResponseDTO.setCreatedAt(LocalDateTime.now());
        categoryResponseDTO.setUpdatedAt(LocalDateTime.now());
        categoryResponseDTO.setIsDeleted(false);
    }

    @Test
    void testCreateCategory_Success() {
        // Mock behavior
        when(categoryRepository.existsByName(categoryDTO.getName())).thenReturn(false);
        when(modelMapper.map(categoryDTO, Category.class)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryResponseDTO.class)).thenReturn(categoryResponseDTO);

        // Test
        CategoryResponseDTO result = categoryService.createCategory(categoryDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(categoryResponseDTO.getId(), result.getId());
        assertEquals(categoryResponseDTO.getName(), result.getName());
        verify(categoryRepository).existsByName(categoryDTO.getName());
        verify(modelMapper).map(categoryDTO, Category.class);
        verify(categoryRepository).save(category);
        verify(modelMapper).map(category, CategoryResponseDTO.class);
    }

    @Test
    void testCreateCategory_DuplicateName_ThrowsException() {
        // Mock behavior
        when(categoryRepository.existsByName(categoryDTO.getName())).thenReturn(true);

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                categoryService.createCategory(categoryDTO));

        // Assertions
        assertEquals("Category with name 'Fiction' already exists", exception.getMessage());
        verify(categoryRepository).existsByName(categoryDTO.getName());
        verify(modelMapper, never()).map(any(), any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void testUpdateCategory_Success() {
        // Mock behavior
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("NonFiction")).thenReturn(false);

        // Mock mapping behavior for update (map to existing object)
        doAnswer(invocation -> {
            CategoryDTO source = invocation.getArgument(0);
            Category destination = invocation.getArgument(1);
            destination.setName(source.getName()); // Giả lập ánh xạ
            return null; // Vì map(source, destination) trả về void
        }).when(modelMapper).map(any(CategoryDTO.class), eq(category));

        // Mock mapping from Category to CategoryResponseDTO dynamically
        doAnswer(invocation -> {
            Category source = invocation.getArgument(0);
            CategoryResponseDTO response = new CategoryResponseDTO();
            response.setId(source.getId());
            response.setName(source.getName()); // Đảm bảo ánh xạ name mới
            response.setCreatedAt(source.getCreatedAt());
            response.setUpdatedAt(source.getUpdatedAt());
            response.setIsDeleted(source.getIsDeleted());
            return response;
        }).when(modelMapper).map(category, CategoryResponseDTO.class);

        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setName("NonFiction");

        // Test
        CategoryResponseDTO result = categoryService.updateCategory(1, updatedDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(categoryResponseDTO.getId(), result.getId());
        assertEquals("NonFiction", result.getName()); // Kỳ vọng name mới
        verify(categoryRepository).findById(1);
        verify(categoryRepository).existsByName("NonFiction");
        verify(modelMapper).map(updatedDTO, category);
        verify(categoryRepository).save(category);
        verify(modelMapper).map(category, CategoryResponseDTO.class);
    }

    @Test
    void testUpdateCategory_NotFound_ThrowsException() {
        // Mock behavior
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                categoryService.updateCategory(1, categoryDTO));

        // Assertions
        assertEquals("Category not found with ID: 1", exception.getMessage());
        verify(categoryRepository).findById(1);
        verify(categoryRepository, never()).existsByName(any());
        verify(modelMapper, never()).map(any(), any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void testUpdateCategory_DuplicateName_ThrowsException() {
        // Mock behavior
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("NonFiction")).thenReturn(true);

        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setName("NonFiction");

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                categoryService.updateCategory(1, updatedDTO));

        // Assertions
        assertEquals("Category with name 'NonFiction' already exists", exception.getMessage());
        verify(categoryRepository).findById(1);
        verify(categoryRepository).existsByName("NonFiction");
        verify(modelMapper, never()).map(any(), any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void testGetCategoryById_Success() {
        // Mock behavior
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryResponseDTO.class)).thenReturn(categoryResponseDTO);

        // Test
        CategoryResponseDTO result = categoryService.getCategoryById(1);

        // Assertions
        assertNotNull(result);
        assertEquals(categoryResponseDTO.getId(), result.getId());
        assertEquals(categoryResponseDTO.getName(), result.getName());
        verify(categoryRepository).findById(1);
        verify(modelMapper).map(category, CategoryResponseDTO.class);
    }

    @Test
    void testGetCategoryById_NotFound_ThrowsException() {
        // Mock behavior
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                categoryService.getCategoryById(1));

        // Assertions
        assertEquals("Category not found with ID: 1", exception.getMessage());
        verify(categoryRepository).findById(1);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testGetAllCategories_Success() {
        // Mock behavior
        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));
        when(modelMapper.map(category, CategoryResponseDTO.class)).thenReturn(categoryResponseDTO);

        // Test
        var result = categoryService.getAllCategories();

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(categoryResponseDTO.getId(), result.get(0).getId());
        assertEquals(categoryResponseDTO.getName(), result.get(0).getName());
        verify(categoryRepository).findAll();
        verify(modelMapper).map(category, CategoryResponseDTO.class);
    }

    @Test
    void testGetAllCategories_EmptyList() {
        // Mock behavior
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        // Test
        var result = categoryService.getAllCategories();

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository).findAll();
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testDeleteCategory_Success() {
        // Mock behavior
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        // Test
        categoryService.deleteCategory(1);

        // Assertions
        verify(categoryRepository).findById(1);
        verify(categoryRepository).delete(category);
    }

    @Test
    void testDeleteCategory_NotFound_ThrowsException() {
        // Mock behavior
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                categoryService.deleteCategory(1));

        // Assertions
        assertEquals("Category not found with ID: 1", exception.getMessage());
        verify(categoryRepository).findById(1);
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void testFindCategoryById_Success() {
        // Mock behavior
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        // Test
        Optional<Category> result = categoryService.findCategoryById(1);

        // Assertions
        assertTrue(result.isPresent());
        assertEquals(category.getId(), result.get().getId());
        assertEquals(category.getName(), result.get().getName());
        verify(categoryRepository).findById(1);
    }

    @Test
    void testFindCategoryById_NotFound() {
        // Mock behavior
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        Optional<Category> result = categoryService.findCategoryById(1);

        // Assertions
        assertFalse(result.isPresent());
        verify(categoryRepository).findById(1);
    }
}