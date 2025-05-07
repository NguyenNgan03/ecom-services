package com.example.bookstore.service;

import com.example.bookstore.dto.request.ProductReviewDTO;
import com.example.bookstore.dto.response.ProductReviewResponseDTO;
import com.example.bookstore.entity.Product;
import com.example.bookstore.entity.ProductReview;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.ProductRepository;
import com.example.bookstore.repository.ProductReviewRepository;
import com.example.bookstore.repository.UserRepository;
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
public class ProductReviewServiceImplTest {

    @Mock
    private ProductReviewRepository productReviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductReviewServiceImpl productReviewService;

    private User user;
    private Product product;
    private ProductReview review;
    private ProductReviewDTO reviewDTO;
    private ProductReviewResponseDTO reviewResponseDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setAverageRating(BigDecimal.ZERO);

        review = new ProductReview();
        review.setId(1);
        review.setProduct(product);
        review.setUser(user);
        review.setRating(5);
        review.setComment("Great product!");
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        reviewDTO = new ProductReviewDTO();
        reviewDTO.setProductId(1);
        reviewDTO.setRating(5);
        reviewDTO.setComment("Great product!");

        reviewResponseDTO = new ProductReviewResponseDTO();
        reviewResponseDTO.setId(1);
        reviewResponseDTO.setProductId(1);
        reviewResponseDTO.setProductName("Test Product");
        reviewResponseDTO.setUserId(1);
        reviewResponseDTO.setUserName("John Doe");
        reviewResponseDTO.setRating(5);
        reviewResponseDTO.setComment("Great product!");
        reviewResponseDTO.setCreatedAt(LocalDateTime.now());
        reviewResponseDTO.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateReview_Success() {
        // Mock behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productReviewRepository.existsByProductIdAndUserId(1, 1)).thenReturn(false);
        when(productReviewRepository.save(any(ProductReview.class))).thenReturn(review);
        when(modelMapper.map(review, ProductReviewResponseDTO.class)).thenReturn(reviewResponseDTO);
        when(productReviewRepository.findByProductId(1)).thenReturn(Collections.singletonList(review));
        when(productRepository.save(product)).thenReturn(product);

        // Test
        ProductReviewResponseDTO result = productReviewService.createReview("test@example.com", reviewDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(reviewResponseDTO.getId(), result.getId());
        assertEquals(reviewResponseDTO.getProductId(), result.getProductId());
        assertEquals(reviewResponseDTO.getUserId(), result.getUserId());
        assertEquals(reviewResponseDTO.getRating(), result.getRating());
        assertEquals(reviewResponseDTO.getComment(), result.getComment());
        verify(userRepository).findByEmail("test@example.com");
        verify(productRepository).findById(1);
        verify(productReviewRepository).existsByProductIdAndUserId(1, 1);
        verify(productReviewRepository).save(any(ProductReview.class));
        verify(productReviewRepository).findByProductId(1);
        verify(productRepository).save(product);
        verify(modelMapper).map(review, ProductReviewResponseDTO.class);
    }

    @Test
    void testCreateReview_UserNotFound_ThrowsException() {
        // Mock behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productReviewService.createReview("test@example.com", reviewDTO));

        // Assertions
        assertEquals("User not found with email: test@example.com", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(productRepository, never()).findById(anyInt());
        verify(productReviewRepository, never()).save(any());
    }

    @Test
    void testCreateReview_ProductNotFound_ThrowsException() {
        // Mock behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productReviewService.createReview("test@example.com", reviewDTO));

        // Assertions
        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(productRepository).findById(1);
        verify(productReviewRepository, never()).save(any());
    }

    @Test
    void testCreateReview_AlreadyReviewed_ThrowsException() {
        // Mock behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productReviewRepository.existsByProductIdAndUserId(1, 1)).thenReturn(true);

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productReviewService.createReview("test@example.com", reviewDTO));

        // Assertions
        assertEquals("User has already reviewed this product", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(productRepository).findById(1);
        verify(productReviewRepository).existsByProductIdAndUserId(1, 1);
        verify(productReviewRepository, never()).save(any());
    }

    @Test
    void testCreateReview_NoRatingOrComment_ThrowsException() {
        // Mock behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productReviewRepository.existsByProductIdAndUserId(1, 1)).thenReturn(false);

        ProductReviewDTO invalidDTO = new ProductReviewDTO();
        invalidDTO.setProductId(1);
        invalidDTO.setRating(null);
        invalidDTO.setComment("");

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productReviewService.createReview("test@example.com", invalidDTO));

        // Assertions
        assertEquals("Review must include at least a rating or a comment", exception.getMessage());
        verify(userRepository).findByEmail("test@example.com");
        verify(productRepository).findById(1);
        verify(productReviewRepository).existsByProductIdAndUserId(1, 1);
        verify(productReviewRepository, never()).save(any());
    }

    @Test
    void testUpdateReview_Success() {
        // Mock behavior
        when(productReviewRepository.findById(1)).thenReturn(Optional.of(review));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productReviewRepository.save(review)).thenReturn(review);
        when(productReviewRepository.findByProductId(1)).thenReturn(Collections.singletonList(review));
        when(productRepository.save(product)).thenReturn(product);

        // Mock mapping dynamically
        doAnswer(invocation -> {
            ProductReview source = invocation.getArgument(0);
            ProductReviewResponseDTO response = new ProductReviewResponseDTO();
            response.setId(source.getId());
            response.setProductId(source.getProduct().getId());
            response.setProductName(source.getProduct().getName());
            response.setUserId(source.getUser().getId());
            response.setUserName(source.getUser().getFirstName() + " " + source.getUser().getLastName());
            response.setRating(source.getRating());
            response.setComment(source.getComment());
            response.setCreatedAt(source.getCreatedAt());
            response.setUpdatedAt(source.getUpdatedAt());
            return response;
        }).when(modelMapper).map(review, ProductReviewResponseDTO.class);

        ProductReviewDTO updatedDTO = new ProductReviewDTO();
        updatedDTO.setProductId(1);
        updatedDTO.setRating(4);
        updatedDTO.setComment("Updated comment");

        // Test
        ProductReviewResponseDTO result = productReviewService.updateReview(1, 1, updatedDTO);

        // Assertions
        assertNotNull(result);
        assertEquals(4, result.getRating());
        assertEquals("Updated comment", result.getComment());
        verify(productReviewRepository).findById(1);
        verify(productRepository).findById(1);
        verify(productReviewRepository).save(review);
        verify(productReviewRepository).findByProductId(1);
        verify(productRepository).save(product);
        verify(modelMapper).map(review, ProductReviewResponseDTO.class);
    }

    @Test
    void testUpdateReview_ReviewNotFound_ThrowsException() {
        // Mock behavior
        when(productReviewRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productReviewService.updateReview(1, 1, reviewDTO));

        // Assertions
        assertEquals("Review not found with ID: 1", exception.getMessage());
        verify(productReviewRepository).findById(1);
        verify(productRepository, never()).findById(anyInt());
        verify(productReviewRepository, never()).save(any());
    }

    @Test
    void testUpdateReview_UnauthorizedUser_ThrowsException() {
        // Mock behavior
        when(productReviewRepository.findById(1)).thenReturn(Optional.of(review));

        // Test with different user ID
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productReviewService.updateReview(1, 2, reviewDTO));

        // Assertions
        assertEquals("User not authorized to update this review", exception.getMessage());
        verify(productReviewRepository).findById(1);
        verify(productRepository, never()).findById(anyInt());
        verify(productReviewRepository, never()).save(any());
    }

    @Test
    void testGetReviewById_Success() {
        // Mock behavior
        when(productReviewRepository.findById(1)).thenReturn(Optional.of(review));
        when(modelMapper.map(review, ProductReviewResponseDTO.class)).thenReturn(reviewResponseDTO);

        // Test
        ProductReviewResponseDTO result = productReviewService.getReviewById(1);

        // Assertions
        assertNotNull(result);
        assertEquals(reviewResponseDTO.getId(), result.getId());
        verify(productReviewRepository).findById(1);
        verify(modelMapper).map(review, ProductReviewResponseDTO.class);
    }

    @Test
    void testGetReviewById_NotFound_ThrowsException() {
        // Mock behavior
        when(productReviewRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productReviewService.getReviewById(1));

        // Assertions
        assertEquals("Review not found with ID: 1", exception.getMessage());
        verify(productReviewRepository).findById(1);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testGetReviewsByProductId_Success() {
        // Mock behavior
        when(productReviewRepository.findByProductId(1)).thenReturn(Collections.singletonList(review));
        when(modelMapper.map(review, ProductReviewResponseDTO.class)).thenReturn(reviewResponseDTO);

        // Test
        List<ProductReviewResponseDTO> result = productReviewService.getReviewsByProductId(1);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reviewResponseDTO.getId(), result.get(0).getId());
        verify(productReviewRepository).findByProductId(1);
        verify(modelMapper).map(review, ProductReviewResponseDTO.class);
    }

    @Test
    void testGetReviewsByProductId_EmptyList() {
        // Mock behavior
        when(productReviewRepository.findByProductId(1)).thenReturn(Collections.emptyList());

        // Test
        List<ProductReviewResponseDTO> result = productReviewService.getReviewsByProductId(1);

        // Assertions
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productReviewRepository).findByProductId(1);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testDeleteReview_Success() {
        // Mock behavior
        when(productReviewRepository.findById(1)).thenReturn(Optional.of(review));
        when(productReviewRepository.findByProductId(1)).thenReturn(Collections.emptyList());
        when(productRepository.save(product)).thenReturn(product);

        // Test
        productReviewService.deleteReview(1, 1);

        // Assertions
        verify(productReviewRepository).findById(1);
        verify(productReviewRepository).delete(review);
        verify(productReviewRepository).findByProductId(1);
        verify(productRepository).save(product);
    }

    @Test
    void testDeleteReview_ReviewNotFound_ThrowsException() {
        // Mock behavior
        when(productReviewRepository.findById(1)).thenReturn(Optional.empty());

        // Test
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productReviewService.deleteReview(1, 1));

        // Assertions
        assertEquals("Review not found with ID: 1", exception.getMessage());
        verify(productReviewRepository).findById(1);
        verify(productReviewRepository, never()).delete(any());
    }

    @Test
    void testDeleteReview_UnauthorizedUser_ThrowsException() {
        // Mock behavior
        when(productReviewRepository.findById(1)).thenReturn(Optional.of(review));

        // Test with different user ID
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                productReviewService.deleteReview(1, 2));

        // Assertions
        assertEquals("User not authorized to delete this review", exception.getMessage());
        verify(productReviewRepository).findById(1);
        verify(productReviewRepository, never()).delete(any());
    }
}