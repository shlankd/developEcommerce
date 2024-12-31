package com.example.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Product in the ecommerce. */
@Entity
@Table(name = "product")
public class Product {

    /** Id of the product (to make unique). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** The name of the product. */
    @NotNull
    @NotBlank
    @Column(name = "product_name", nullable = false, unique = true)
    private String name;

    /** The description of the product. */
    @NotNull
    @NotBlank
    @Column(name = "product_description", nullable = false)
    private String description;

    /** The price of the product. */
    @NotNull
    @Min(value = 0)
    @Column(name = "price", nullable = false)
    private Double price;

    /** The quantity of the product. */
    @NotNull
    @Min(value = 0)
    @Column(name = "product_quantity", nullable = false)
    private Integer productQuantity;

    // Getters and Setters.

    /**
     * Gets the quantity of the product in the inventory.
     * @return Returns quantity of the product in the inventory.
     */
    public Integer getProductQuantity() {
        return productQuantity;
    }

    /**
     * Sets the quantity of the product in the inventory.
     * @param quantity The quantity of the product in the inventory to set.
     */
    public void setProductQuantity(Integer quantity) {
        this.productQuantity = quantity;
    }

    /**
     * Gets the product's price.
     * @return Returns product's price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the product's price.
     * @param price The product's price to set.
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Gets the product's description.
     * @return Returns the product's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the product's description.
     * @param description The product's description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the product's name.
     * @return name Returns the product's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product's name.
     * @param name The product's name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the product's id.
     * @return Returns product's id.
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * Sets the product's id.
     * @param id The product's id to set.
     */
    public void setProductId(Long id) {
        this.productId = id;
    }

}