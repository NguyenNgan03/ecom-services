package com.example.bookstore.repository;
import com.example.bookstore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Find all featured products (isFeatured = true)
    List<Product> findByIsFeaturedTrue();

    // Find all products by category ID
    List<Product> findByCategoryId(Integer categoryId);

    // Check if a product with the given name already exists (for validation)
    boolean existsByName(String name);

    //Find by name

    Product findByName(String name);
}