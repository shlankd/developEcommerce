package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

/**
 * An interface class to create data access for objects of Product data.
 */
public interface ProductRepository extends ListCrudRepository<Product, Long> {
    //Optional<Product> findByProductName(String productName);

    Optional<Product> findProductByName(String productName);
}
