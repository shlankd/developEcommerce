package com.example.ecommerce.service;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ProductIdNotExistsException;
import com.example.ecommerce.exception.ProductNameAlReadyExistException;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

/**
 * This class tests methods from the ProductService class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceTest {

    /** The Product Service instance. */
    @Autowired
    private ProductService productService;

    /** The Product Repository instance. */
    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    public void testAddProductWithProductNameThatExists() {
        Product testProduct= new Product();

        testProduct.setName("Product Test #1");
        testProduct.setDescription("description to product test 1.");
        testProduct.setPrice(80.0);
        testProduct.setProductQuantity(76);

        // Test the addProduct throw exception ProductNameAlReadyExistException with an exists product name.
        Assertions.assertThrows(ProductNameAlReadyExistException.class,
                () -> productService.addProduct(testProduct), "The product name should be already exists.");
    }

    @Test
    @Transactional
    public void testAddValidProduct() {
        Product testProduct= new Product();

        testProduct.setName("Product Test #6");
        testProduct.setDescription("description to product test 6.");
        testProduct.setPrice(80.0);
        testProduct.setProductQuantity(76);

        // Test the addProduct with no exception.
        Assertions.assertDoesNotThrow(() -> productService.addProduct(testProduct),
                "The product should be added successful without exception.");
    }

    @Test
    @Transactional
    public void testUpdateProductWithNotExistsId(){
        Product testProduct= new Product();

        testProduct.setName("Product Test #1 Updated");
        testProduct.setDescription("description to product test 1 updated.");
        testProduct.setPrice(80.0);
        testProduct.setProductQuantity(76);

        // Test the updateProduct throw exception ProductIdNotExistsException with not exists product id.
        Assertions.assertThrows(ProductIdNotExistsException.class,
                () -> productService.updateProduct(testProduct, 6L), "The update product id should not exists.");
    }

    @Test
    @Transactional
    public void testUpdateProductWithExistsProductName(){
        Product testProduct= new Product();

        testProduct.setName("Product Test #3");
        testProduct.setDescription("description to product test 1 updated.");
        testProduct.setPrice(80.0);
        testProduct.setProductQuantity(76);

        // Test the updateProduct throw exception ProductNameAlReadyExistException with product name that exists in other product id.
        Assertions.assertThrows(ProductNameAlReadyExistException.class,
                () -> productService.updateProduct(testProduct, 1L), "The update product name should be already exists.");
    }

    @Test
    @Transactional
    public void testUpdateProductWithNoExceptionThrow(){

        // Product name from data sql.
        Optional<Product> opTestProductUpdate= productRepository.findProductByName(
                "Product Test #1");

        Assertions.assertTrue(opTestProductUpdate.isPresent(),
                "The Product Test #1 should be present.");

        Product testProductUpdate = opTestProductUpdate.get();
        testProductUpdate.setName("Product Test #1");
        testProductUpdate.setDescription("description to product test 1 updated.");
        testProductUpdate.setPrice(80.0);
        testProductUpdate.setProductQuantity(76);

        // Test the updateProduct with no exception.
        Assertions.assertDoesNotThrow(() ->
                        productService.updateProduct(testProductUpdate, testProductUpdate.getProductId()),
                "The product should be updated successful without exception.");
    }

    @Test
    @Transactional
    public void testDeleteProductWithNotExistsProductId(){
        // Test the deleteProduct throw exception ProductIdNotExistsException with product id that not exists.
        Assertions.assertThrows(ProductIdNotExistsException.class,
                () -> productService.deleteProductById(6L), "The id of the requested product to delete should not be exists.");
    }

    @Test
    @Transactional
    public void testDeleteProductWithNoException(){
        // Test the deleteProduct with no exception.
        Assertions.assertDoesNotThrow(() -> productService.deleteProductById(4L),
                "The product should be deleted successful without exception.");
    }

}
