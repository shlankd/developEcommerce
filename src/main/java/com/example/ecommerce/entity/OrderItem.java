package com.example.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/** The quantity ordered for a certain product. */
@Entity
@Table(name = "order_item")
public class OrderItem {

    /** Id for the ordered item (to make unique). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** The ordered item product associated. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** The quantity of the ordered item. */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /** The order that associates with the ordered item. */
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private EcommOrder order;

    // Getters and Setters.

    /**
     * Gets the order that associated with the ordered item.
     * @return order Returns the order of the ordered item.
     */
    public EcommOrder getOrder() {
        return order;
    }

    /**
     * Sets the order association with the ordered item.
     * @param order The order to set the ordered item.
     */
    public void setOrder(EcommOrder order) {
        this.order = order;
    }

    /**
     * Gets the quantity of the ordered item.
     * @return quantity Returns the quantity of the ordered item.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the ordered item.
     * @param quantity The quantity to set of the ordered item.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the product of the ordered item.
     * @return product Returns the product of the ordered item.
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product ordered item.
     * @param product The product to set the ordered item.
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Gets the ordered item id.
     * @return Returns the id of the ordered item.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ordered item id.
     * @param id The id to set the ordered item.
     */
    public void setId(Long id) {
        this.id = id;
    }

}