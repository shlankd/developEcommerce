//package com.example.ecommerce.api_model;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//
///**
// * The information that needs to fill to create an order.
// */
//public class OrderSetBody {
//
//    @NotNull
//    private Long userId;
//
//    @NotNull
//    private Long addressId;
//
//    @NotNull
//    @NotBlank
//    private String currency;
//
//    private String description;
//
//    /** Getters and Setters */
//
//    public @NotNull Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(@NotNull Long userId) {
//        this.userId = userId;
//    }
//
//
//
//    public @NotNull Long getAddressId() {
//        return addressId;
//    }
//
//    public void setAddressId(@NotNull Long addressId) {
//        this.addressId = addressId;
//    }
//
//    public @NotNull @NotBlank String getCurrency() {
//        return currency;
//    }
//
//    public void setCurrency(@NotNull @NotBlank String currency) {
//        this.currency = currency;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//}
