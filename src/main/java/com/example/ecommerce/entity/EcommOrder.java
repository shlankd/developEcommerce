package com.example.ecommerce.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/** the order of the ecommerce entity. */
@Entity
@Table(name = "ecomm_order")
public class EcommOrder {

    /** Id for the order (to make unique). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** The user's order associated. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private EcommUser user;

    /** The user's address to delivery in the order associated. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    /** Order's quantity associated with the order quantity entity. */
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    /** Order's total price of payment. */
    @Column(name = "order_total_payment")
    private Double orderTotalPayment = 0.0;

    // Getters and Setters.

    /**
     * Gets the total price of this order for payment.
     * @return Returns the total payment of this order.
     */
    public Double getOrderTotalPayment() {
        return orderTotalPayment;
    }

    /**
     * Sets the total payment of this order.
     * @param orderTotalPayment The value of the total payment price of this order.
     */
    public void setOrderTotalPayment(Double orderTotalPayment) {
        this.orderTotalPayment = orderTotalPayment;
    }

    /**
     * Gets the quantities of the order.
     * @return Returns order's quantities.
     */
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    /**
     * Sets the quantities of the order.
     * @param orderItems  The order's quantities to set.
     */
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * Gets order's address.
     * @return Returns order's address.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the order's address.
     * @param address The order's address to set.
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Gets the order's user.
     * @return Returns Order's user.
     */
    public EcommUser getUser() {
        return user;
    }

    /**
     * Sets the order's user.
     * @param user The order's user to set.
     */
    public void setUser(EcommUser user) {
        this.user = user;
    }

    /**
     * Gets the order's id.
     * @return Returns order's id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the order's id
     * @param id The order's id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

}