package com.example.ecommerce.service;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ProductNameAlReadyExistException;
import com.example.ecommerce.exception.ProductIdNotExistsException;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This class contains methods that service product operations.
 */
@Service
public class ProductService {

    /** The Product Repository. */
    private ProductRepository productRepository;

    /**
     * The ProductService Constructor.
     * @param productRepository The ProductRepository object.
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Gets all available products.
     * @return The list of orders.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Gets the product by id.
     * @param productId The product's id.
     * @return The product with the given id.
     */
    public Product getProductById(Long productId) throws ProductIdNotExistsException {
        Optional<Product> opProduct = productRepository.findById(productId);
        if (opProduct.isPresent()) {
            return opProduct.get();
        }
        throw new ProductIdNotExistsException();
    }

    /**
     * Creates new product.
     * @param newProductToAdd The new Product object to add.
     * @return The created product.
     */
    public Product addProduct(Product newProductToAdd) throws ProductNameAlReadyExistException {

        // Condition if the name of the new product to add is already exist.
        if(productRepository.findProductByName(newProductToAdd.getName()).isPresent()){
            throw new ProductNameAlReadyExistException();
        }

        return productRepository.save(newProductToAdd);
    }

    /**
     * Updates product that exists.
     * @param productId product's id.
     * @param productDetails The product to update.
     * @return The updated product if exists else throws ProductNotExistsException.
     */
    public Product updateProduct(Product productDetails, Long productId) throws ProductIdNotExistsException, ProductNameAlReadyExistException {

        // Finds the product that requested to update by the product id.
        Optional<Product> opProduct = productRepository.findById(productId);

        // Condition If the product exists.
        if(opProduct.isPresent()) {

            Product productToUpdate = opProduct.get();

            // Finds product from the repository by the product name of the update details.
            Optional<Product> opProductByName = productRepository.findProductByName(productDetails.getName());

            // Condition if the product name of the update details exist from the repository
            // and has different id from the requested updated product.
            if(opProductByName.isPresent() && opProductByName.get().getProductId() != productToUpdate.getProductId()){
                throw new ProductNameAlReadyExistException();
            }

            return productRepository.save(productDetails);
        }
        throw new ProductIdNotExistsException();
    }

    /**
     * Deletes an exists product. If the requested product id not exists the throws ProductNotExistsException.
     * @param id The product's id to delete.
     */
    public void deleteProductById(Long id) throws ProductIdNotExistsException {
        Optional<Product> opProduct = productRepository.findById(id);

        if(opProduct.isPresent()) {
            productRepository.delete(opProduct.get());
        }
        else{
            throw new ProductIdNotExistsException();
        }
    }

}
