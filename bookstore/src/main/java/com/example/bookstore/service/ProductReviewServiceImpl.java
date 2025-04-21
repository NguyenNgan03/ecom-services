package com.example.bookstore.service;

import com.example.bookstore.dto.request.ProductReviewDTO;
import com.example.bookstore.dto.response.ProductReviewResponseDTO;
import com.example.bookstore.entity.Product;
import com.example.bookstore.entity.ProductReview;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.ProductRepository;
import com.example.bookstore.repository.ProductReviewRepository;
import com.example.bookstore.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductReviewServiceImpl(ProductReviewRepository productReviewRepository,
                                    ProductRepository productRepository,
                                    UserRepository userRepository,
                                    ModelMapper modelMapper) {
        this.productReviewRepository = productReviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;

        // Custom mapping for ProductReview to ProductReviewResponseDTO
        modelMapper.addMappings(new PropertyMap<ProductReview, ProductReviewResponseDTO>() {
            @Override
            protected void configure() {
                map(source.getProduct().getId(), destination.getProductId());
                map(source.getProduct().getName(), destination.getProductName());
                map(source.getUser().getId(), destination.getUserId());
                using(ctx -> {
                    User user = ((ProductReview) ctx.getSource()).getUser();
                    return user.getFirstName() + " " + user.getLastName();
                }).map(source, destination.getUserName());
            }
        });
    }

    @Override
    public ProductReviewResponseDTO createReview(Integer userId, ProductReviewDTO request) {
        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Validate product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + request.getProductId()));

        // Check if user already reviewed this product
        if (productReviewRepository.existsByProductIdAndUserId(request.getProductId(), userId)) {
            throw new RuntimeException("User has already reviewed this product");
        }

        // Validate that at least rating or comment is provided
        if (request.getRating() == null && (request.getComment() == null || request.getComment().trim().isEmpty())) {
            throw new RuntimeException("Review must include at least a rating or a comment");
        }

        // Map DTO to entity
        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        // Save review
        ProductReview savedReview = productReviewRepository.save(review);

        // Update product's average rating
        updateProductAverageRating(product);

        return modelMapper.map(savedReview, ProductReviewResponseDTO.class);
    }

    @Override
    public ProductReviewResponseDTO updateReview(Integer id, Integer userId, ProductReviewDTO request) {
        // Find existing review
        ProductReview review = productReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));

        // Validate user ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to update this review");
        }

        // Validate product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + request.getProductId()));

        // Validate that at least rating or comment is provided
        if (request.getRating() == null && (request.getComment() == null || request.getComment().trim().isEmpty())) {
            throw new RuntimeException("Review must include at least a rating or a comment");
        }

        // Update review
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        // Save updated review
        ProductReview updatedReview = productReviewRepository.save(review);

        // Update product's average rating
        updateProductAverageRating(product);

        return modelMapper.map(updatedReview, ProductReviewResponseDTO.class);
    }

    @Override
    public ProductReviewResponseDTO getReviewById(Integer id) {
        ProductReview review = productReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));
        return modelMapper.map(review, ProductReviewResponseDTO.class);
    }

    @Override
    public List<ProductReviewResponseDTO> getReviewsByProductId(Integer productId) {
        return productReviewRepository.findByProductId(productId).stream()
                .map(review -> modelMapper.map(review, ProductReviewResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(Integer id, Integer userId) {
        // Find existing review
        ProductReview review = productReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + id));

        // Validate user ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("User not authorized to delete this review");
        }

        // Delete review
        productReviewRepository.delete(review);

        // Update product's average rating
        Product product = review.getProduct();
        updateProductAverageRating(product);
    }

    private void updateProductAverageRating(Product product) {
        List<ProductReview> reviews = productReviewRepository.findByProductId(product.getId());
        if (reviews.isEmpty()) {
            product.setAverageRating(BigDecimal.ZERO);
        } else {
            BigDecimal total = reviews.stream()
                    .filter(review -> review.getRating() != null) // Only include reviews with ratings
                    .map(review -> BigDecimal.valueOf(review.getRating()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            int count = (int) reviews.stream().filter(review -> review.getRating() != null).count();
            product.setAverageRating(count > 0 ? total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        }
        productRepository.save(product);
    }
}