package com.example.bookstore.service;

import com.example.bookstore.dto.request.ProductDTO;
import com.example.bookstore.dto.response.ProductResponseDTO;
import com.example.bookstore.entity.Category;
import com.example.bookstore.entity.Product;
import com.example.bookstore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;
    private ProductResponseDTO productResponseDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        // Initialize test data
        category = new Category();
        category.setId(1);
        category.setName("Fiction");

        product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(29.99));
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setIsDeleted(false);
        product.setIsFeatured(false);

        productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setPrice(BigDecimal.valueOf(29.99));
        productDTO.setCategoryId(1);

        productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(1);
        productResponseDTO.setName("Test Product");
        productResponseDTO.setPrice(BigDecimal.valueOf(29.99));
        productResponseDTO.setCategoryName("Fiction");
        productResponseDTO.setCreatedAt(LocalDateTime.now());
        productResponseDTO.setUpdatedAt(LocalDateTime.now());
        productResponseDTO.setIsDeleted(false);
//        productResponseDTO.setIsFeatured(false);
    }

    @Test
    void testCreateProduct_Success() {
        // Mock behavior
        when(categoryService.findCategoryById(1)).thenReturn(Optional.of(category));
        when(productRepository.existsByName("Test Product")).thenReturn(false);
        when(modelMapper.map(productDTO, Product.class)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(modelMapper.map(product, ProductResponseDTO.class)).thenReturn(productResponseDTO);

        // Test
        ProductResponseDTO result = productService.createProduct(productDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(productResponseDTO.getId(), result.getId());
        assertEquals(productResponseDTO.getName(), result.getName());
        assertEquals(productResponseDTO.getCategoryName(), result.getCategoryName());
        verify(categoryService).findCategoryById(1);
        verify(productRepository).existsByName("Test Product");
        verify(modelMapper).map(productDTO, Product.class);
        verify(productRepository).save(product);
        verify(modelMapper).map(product, ProductResponseDTO.class);
    }

    @Test
    void testCreateProduct_CategoryNotFound_ThrowsException() {
        // Mock behavior
        when(categoryService.findCategoryById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productService.createProduct(productDTO));

        // Assertions
        assertEquals("Category not found with ID: 1", exception.getMessage());
        verify(categoryService).findCategoryById(1);
        verify(productRepository, never()).existsByName(anyString());
        verify(modelMapper, never()).map(any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testCreateProduct_DuplicateName_ThrowsException() {
        // Mock behavior
        when(categoryService.findCategoryById(1)).thenReturn(Optional.of(category));
        when(productRepository.existsByName("Test Product")).thenReturn(true);

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productService.createProduct(productDTO));

        // Assertions
        assertEquals("Product with name 'Test Product' already exists", exception.getMessage());
        verify(categoryService).findCategoryById(1);
        verify(productRepository).existsByName("Test Product");
        verify(modelMapper, never()).map(any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testUpdateProduct_Success() {
        // Mock behavior
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(categoryService.findCategoryById(1)).thenReturn(Optional.of(category));
        when(productRepository.existsByName("Updated Product")).thenReturn(false);

        // Mock mapping behavior for update (map to existing object)
        doAnswer(invocation -> {
            ProductDTO source = invocation.getArgument(0);
            Product destination = invocation.getArgument(1);
            destination.setName(source.getName());
            destination.setPrice(source.getPrice());
            return null; // Vì map(source, destination) trả về void
        }).when(modelMapper).map(any(ProductDTO.class), eq(product));

        // Mock mapping from Product to ProductResponseDTO dynamically
        doAnswer(invocation -> {
            Product source = invocation.getArgument(0);
            ProductResponseDTO response = new ProductResponseDTO();
            response.setId(source.getId());
            response.setName(source.getName());
            response.setPrice(source.getPrice());
            response.setCategoryName(source.getCategory().getName());
            response.setCreatedAt(source.getCreatedAt());
            response.setUpdatedAt(source.getUpdatedAt());
            response.setIsDeleted(source.getIsDeleted());
//            response.setIsFeatured(source.getIsFeatured());
            return response;
        }).when(modelMapper).map(product, ProductResponseDTO.class);

        when(productRepository.save(product)).thenReturn(product);

        ProductDTO updatedDTO = new ProductDTO();
        updatedDTO.setName("Updated Product");
        updatedDTO.setPrice(BigDecimal.valueOf(39.99));
        updatedDTO.setCategoryId(1);

        // Test
        ProductResponseDTO result = productService.updateProduct(1, updatedDTO);

        // Assertions
        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals(BigDecimal.valueOf(39.99), result.getPrice());
        assertEquals("Fiction", result.getCategoryName());
        verify(productRepository).findById(1);
        verify(categoryService).findCategoryById(1);
        verify(productRepository).existsByName("Updated Product");
        verify(modelMapper).map(updatedDTO, product);
        verify(productRepository).save(product);
        verify(modelMapper).map(product, ProductResponseDTO.class);
    }

    @Test
    void testUpdateProduct_ProductNotFound_ThrowsException() {
        // Mock behavior
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productService.updateProduct(1, productDTO));

        // Assertions
        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(productRepository).findById(1);
        verify(categoryService, never()).findCategoryById(anyInt());
        verify(productRepository, never()).existsByName(anyString());
        verify(modelMapper, never()).map(any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testUpdateProduct_DuplicateName_ThrowsException() {
        // Mock behavior
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(categoryService.findCategoryById(1)).thenReturn(Optional.of(category));
        when(productRepository.existsByName("Updated Product")).thenReturn(true);

        ProductDTO updatedDTO = new ProductDTO();
        updatedDTO.setName("Updated Product");
        updatedDTO.setPrice(BigDecimal.valueOf(39.99));
        updatedDTO.setCategoryId(1);

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productService.updateProduct(1, updatedDTO));

        // Assertions
        assertEquals("Product with name 'Updated Product' already exists", exception.getMessage());
        verify(productRepository).findById(1);
        verify(categoryService).findCategoryById(1);
        verify(productRepository).existsByName("Updated Product");
        verify(modelMapper, never()).map(any(), any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void testGetProductById_Success() {
        // Mock behavior
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(modelMapper.map(product, ProductResponseDTO.class)).thenReturn(productResponseDTO);

        // Test
        ProductResponseDTO result = productService.getProductById(1);

        // Assertions
        assertNotNull(result);
        assertEquals(productResponseDTO.getId(), result.getId());
        assertEquals(productResponseDTO.getName(), result.getName());
        verify(productRepository).findById(1);
        verify(modelMapper).map(product, ProductResponseDTO.class);
    }

    @Test
    void testGetProductById_NotFound_ThrowsException() {
        // Mock behavior
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productService.getProductById(1));

        // Assertions
        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(productRepository).findById(1);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testGetAllProducts_Success() {
        // Mock behavior
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));
        when(modelMapper.map(product, ProductResponseDTO.class)).thenReturn(productResponseDTO);

        // Test
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productResponseDTO.getId(), result.get(0).getId());
        verify(productRepository).findAll();
        verify(modelMapper).map(product, ProductResponseDTO.class);
    }

    @Test
    void testGetAllProducts_EmptyList() {
        // Mock behavior
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // Test
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findAll();
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testGetFeaturedProducts_Success() {
        // Mock behavior
        product.setIsFeatured(true);
        when(productRepository.findByIsFeaturedTrue()).thenReturn(Collections.singletonList(product));
        when(modelMapper.map(product, ProductResponseDTO.class)).thenReturn(productResponseDTO);

        // Test
        List<ProductResponseDTO> result = productService.getFeaturedProducts();

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productResponseDTO.getId(), result.get(0).getId());
        verify(productRepository).findByIsFeaturedTrue();
        verify(modelMapper).map(product, ProductResponseDTO.class);
    }

    @Test
    void testGetFeaturedProducts_EmptyList() {
        // Mock behavior
        when(productRepository.findByIsFeaturedTrue()).thenReturn(Collections.emptyList());

        // Test
        List<ProductResponseDTO> result = productService.getFeaturedProducts();

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository).findByIsFeaturedTrue();
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testGetProductsByCategory_Success() {
        // Mock behavior
        when(categoryService.findCategoryById(1)).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryId(1)).thenReturn(Collections.singletonList(product));
        when(modelMapper.map(product, ProductResponseDTO.class)).thenReturn(productResponseDTO);

        // Test
        List<ProductResponseDTO> result = productService.getProductsByCategory(1);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productResponseDTO.getId(), result.get(0).getId());
        verify(categoryService).findCategoryById(1);
        verify(productRepository).findByCategoryId(1);
        verify(modelMapper).map(product, ProductResponseDTO.class);
    }

    @Test
    void testGetProductsByCategory_CategoryNotFound_ThrowsException() {
        // Mock behavior
        when(categoryService.findCategoryById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productService.getProductsByCategory(1));

        // Assertions
        assertEquals("Category not found with ID: 1", exception.getMessage());
        verify(categoryService).findCategoryById(1);
        verify(productRepository, never()).findByCategoryId(anyInt());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testDeleteProduct_Success() {
        // Mock behavior
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        // Test
        productService.deleteProduct(1);

        // Assertions
        verify(productRepository).findById(1);
        verify(productRepository).delete(product);
    }

    @Test
    void testDeleteProduct_NotFound_ThrowsException() {
        // Mock behavior
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productService.deleteProduct(1));

        // Assertions
        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(productRepository).findById(1);
        verify(productRepository, never()).delete(any());
    }
}