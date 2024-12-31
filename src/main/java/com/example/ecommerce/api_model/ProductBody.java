//package com.example.ecommerce.api_model;
//
//import jakarta.persistence.Column;
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//
///**
// * The information that needs to fill for product.
// */
//public class ProductBody {
//    /** The name of the product. */
//    @NotNull
//    @NotBlank
//    private String name;
//
//    /** The description of the product. */
//    @NotNull
//    @NotBlank
//    private String description;
//
//    /** The price of the product. */
//    @NotNull
//    @Min(value = 0)
//    private Double price;
//
//    /** the quantity of the product. */
//    @NotNull
//    @Min(value = 0)
//    private Integer productQuantity;
//
//    public @NotNull @NotBlank String getName() {
//        return name;
//    }
//
//    public void setName(@NotNull @NotBlank String name) {
//        this.name = name;
//    }
//
//    public @NotNull @NotBlank String getDescription() {
//        return description;
//    }
//
//    public void setDescription(@NotNull @NotBlank String description) {
//        this.description = description;
//    }
//
//    public @NotNull @Min(value = 0) Double getPrice() {
//        return price;
//    }
//
//    public void setPrice(@NotNull @Min(value = 0) Double price) {
//        this.price = price;
//    }
//
//    public @NotNull @Min(value = 0) Integer getProductQuantity() {
//        return productQuantity;
//    }
//
//    public void setProductQuantity(@NotNull @Min(value = 0) Integer productQuantity) {
//        this.productQuantity = productQuantity;
//    }
//}
