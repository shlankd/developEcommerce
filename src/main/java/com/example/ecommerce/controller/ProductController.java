package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ProductNameAlReadyExistException;
import com.example.ecommerce.exception.ProductIdNotExistsException;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Product Controller that handles updating,viewing products and adding new products of ecommerce.
 */
@RestController
public class ProductController {

    /** Instance of ProductService. */
    private ProductService productService;

    /** Instance of SimpMessagingTemplate */
    //private SimpMessagingTemplate messagingTemplate;

    /**
     * Product Controller Constructor.
     * @param productService
     //* @param messagingTemplate
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
        //this.messagingTemplate = messagingTemplate;
    }

    /**
     * This method gets the list of products that are available.
     * @return List of products.
     */
    @GetMapping("/getAllProducts")
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    /**
     * Gets the product by the given product's id.
     * @param productId The product's id.
     * @return The product with the given product id.
     */
    @GetMapping("/getProductById/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {

        try{
            Product product = productService.getProductById(productId);
            return ResponseEntity.ok(product);
        }
        catch(ProductIdNotExistsException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * Post mapping of product creation.
     * @param productToAdd The Product object to add.
     * @return Returns status created if the product creation has done successfully.
     */
    @PostMapping("/admin/productAdd")
    public ResponseEntity<Product> addNewProduct(@Valid @RequestBody Product productToAdd){

        try {
            Product createdProduct = productService.addProduct(productToAdd);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        }catch (ProductNameAlReadyExistException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////

        // Sends the data object of address with type operation INSERT
        // and sends it to the destination "/topic/user/userId/address".
//        messagingTemplate.convertAndSend("/topic/admin/addNewProduct",
//                new DataOperation<>(productToAdd, DataOperation.DataOperatesType.INSERT));

        ///////////////////////////////////////////////////////////////////////////////////////////////

        //return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Put mapping  the update product.
     * @param productId product's id.
     * @param productDetails The details to update the requested product.
     * @return Returns response status ok if the update product done successfully,
     *         returns response status forbidden for product id that not exists,
     *         returns response status conflict if the name of the update product is already exists,
     *         returns response status bad request if the id of the product detail to update doesn't match
     *         with the request product id.
     */
    @PatchMapping("/admin/updateProduct/{productId}")
    public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product productDetails,
                                                 @PathVariable Long productId) {
        if(productDetails.getProductId() == productId) {
            try {
                Product updatedProduct = productService.updateProduct(productDetails, productId);
                return ResponseEntity.ok(updatedProduct);
            }
            // Catch exception when the product id is not exists.
            catch (ProductIdNotExistsException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            // Catch exception when the product update name already exists from the product repository.
            catch (ProductNameAlReadyExistException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Delete mapping for product delete.
     * @param productId The product id to delete.
     * @return Returns response status of ok if the product successfully deleted
     *         otherwise returns response entity forbidden.
     */
    @DeleteMapping("/admin/deleteProductById/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId){

        try {
            productService.deleteProductById(productId);
        }
        // Catch exception when the product id is not exists.
        catch(ProductIdNotExistsException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }

}
