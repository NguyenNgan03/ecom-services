package com.example.bookstore.service;

import com.example.bookstore.dto.request.ProductDTO;
import com.example.bookstore.dto.response.ProductResponseDTO;
import com.example.bookstore.entity.Category;
import com.example.bookstore.entity.Product;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService; // Assumed to exist for category validation
    private final ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;

        // Custom mapping for ProductRequestDTO to Product (categoryId to category)
        modelMapper.addMappings(new PropertyMap<ProductDTO, Product>() {
            @Override
            protected void configure() {
                skip(destination.getId()); // Skip ID field during creation
                skip(destination.getCreatedAt()); // Skip audit fields
                skip(destination.getUpdatedAt());
                skip(destination.getAverageRating());
                skip(destination.getReviews());
                skip(destination.getCartItems());
                skip(destination.getOrderItems());
                skip(destination.getIsDeleted());
                skip(destination.getCategory());
            }
        });

        // Custom mapping for Product to ProductResponseDTO (category.getName() to categoryName)
        modelMapper.addMappings(new PropertyMap<Product, ProductResponseDTO>() {
            @Override
            protected void configure() {
                map(source.getCategory().getName(), destination.getCategoryName());
            }
        });
    }

    @Override
    public ProductResponseDTO createProduct(ProductDTO request) {
        // Validate category
        Category category = categoryService.findCategoryById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.getCategoryId()));

        // Validate product name uniqueness
        if (productRepository.existsByName(request.getName())) {
            throw new RuntimeException("Product with name '" + request.getName() + "' already exists");
        }

        // Map DTO to entity
        Product product = modelMapper.map(request, Product.class);
        product.setCategory(category); // Manually set the category

        // Save product
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    @Override
    public ProductResponseDTO updateProduct(Integer id, ProductDTO request) {
        // Find existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // Validate category
        Category category = categoryService.findCategoryById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.getCategoryId()));

        // Validate product name uniqueness (excluding current product)
        if (!product.getName().equals(request.getName()) && productRepository.existsByName(request.getName())) {
            throw new RuntimeException("Product with name '" + request.getName() + "' already exists");
        }

        // Map DTO to entity (update fields)
        modelMapper.map(request, product);
        product.setCategory(category); // Manually set the category

        // Save updated product
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductResponseDTO.class);
    }

    @Override
    public ProductResponseDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return modelMapper.map(product, ProductResponseDTO.class);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDTO> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrue().stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(Integer categoryId) {
        // Validate category
        categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));

        return productRepository.findByCategoryId(categoryId).stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        productRepository.delete(product); // Soft delete due to @SQLDelete
    }
}
