package com.example.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

/**
 * An entity of cart item that allows the user to manage what items he wants to order.
 */
@Entity
@Table(name = "cart_item")
public class CartItem {

    /** The cart item id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id", nullable = false)
    private Long cartItemId;

    /** The quantity of the selected cart item.  */
    @Min(value = 1)
    @Column(name = "cart_item_quantity", nullable = false)
    private Integer cartItemQuantity;

    /** The user that the cart item is belongs to. */
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private EcommUser user;

    /** The product of the selected cart item. */
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Getters and setters. */

    public EcommUser getUser() { return user; }

    public void setUser(EcommUser user) { this.user = user; }

    public Integer getCartItemQuantity() {
        return cartItemQuantity;
    }

    public void setCartItemQuantity(Integer quantity) {
        this.cartItemQuantity = quantity;
    }

    public Product getProduct() { return product; }

    public void setProduct(Product product) { this.product = product; }

    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long id) {
        this.cartItemId = id;
    }

}