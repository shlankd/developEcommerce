//package com.example.ecommerce.api_model;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//
///**
// * The body set for the user to select item (product) to his cart.
// */
//public class CartItemBody {
//
//    @NotNull
//    @NotBlank
//    private Long selectedProductId;
//
//    @NotNull
//    @NotBlank
//    private Integer selectedProductQuantity;
//
//    public @NotNull @NotBlank Long getSelectedProductId() {
//        return selectedProductId;
//    }
//
//    public void setSelectedProductId(@NotNull @NotBlank Long selectedProductId) {
//        this.selectedProductId = selectedProductId;
//    }
//
//    public @NotNull @NotBlank Integer getSelectedProductQuantity() {
//        return selectedProductQuantity;
//    }
//
//    public void setSelectedProductQuantity(@NotNull @NotBlank Integer selectedProductQuantity) {
//        this.selectedProductQuantity = selectedProductQuantity;
//    }
//}
