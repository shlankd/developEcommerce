//package com.example.ecommerce.entity;
//
//import jakarta.persistence.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "cart")
//public class Cart {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false)
//    private Long id;
//
//    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
//    @JoinColumn(nullable = false, unique = true)
//    private EcommUser user;
//
//    @OneToMany(mappedBy = "cart", cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private List<CartItem> cartItems = new ArrayList<>();
//
//    /** Getters and setters. */
//
//    public List<CartItem> getCartItems() {
//        return cartItems;
//    }
//
//    public void setCartItems(List<CartItem> cartItems) {
//        this.cartItems = cartItems;
//    }
//
//    public EcommUser getUser() {
//        return user;
//    }
//
//    public void setUser(EcommUser user) {
//        this.user = user;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//}