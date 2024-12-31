package com.example.ecommerce.repository;

import com.example.ecommerce.entity.CartItem;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * An interface class to create data access for objects of CartItem data.
 */
public interface CartItemRepository extends ListCrudRepository<CartItem, Long> {
    // Finds the cart of the user by the user's id.
    List<CartItem> findCartItemsByUserId(Long userId);

    // Finds the cart items by user id and product id.
    List<CartItem> findByUser_IdAndProduct_ProductId(Long userId, Long productId);


}
