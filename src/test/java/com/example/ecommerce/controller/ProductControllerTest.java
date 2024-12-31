package com.example.ecommerce.controller;

import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This class tests the ProductController class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    /** This instance mocks MVC, which allows performing HTTP calls. */
    @Autowired
    private MockMvc mvc; // MVC: Model View Controller.

    @Autowired
    private EcommUserRepository ecommUserRepository;

    @Autowired
    private JWTService jwtService;

    private String adminToken;
    private EcommUser testAdmin;
    private ObjectMapper mapper;
    private Product product6;

    @BeforeEach
    void setUp() {

        adminToken = null;

        mapper = new ObjectMapper();

        product6 = new Product();
        product6.setName("Product Test #6");
        product6.setDescription("description to product test 6.");
        product6.setPrice(80.0);
        product6.setProductQuantity(76);

        // Username: "testAdmin" from the data.sql file.
        Optional<EcommUser> opTestAdmin = ecommUserRepository.findByUsernameIgnoreCase("testAdmin");

        if(opTestAdmin.isPresent()) {

            testAdmin = opTestAdmin.get();

            adminToken = jwtService.generateJWT(testAdmin);
        }
    }

    /**
     * This method tests the getProducts method from the ProductController class.
     * @throws Exception
     */
    @Test
    public void testGetAllProducts() throws Exception {

        // Performs HTTP call 'get' on "/product" and expect for the status to be ok.
        mvc.perform(get("/getAllProducts")).andExpect(status().is(HttpStatus.OK.value()));
    }

    /**
     * Test the method getProductById with product id that not exists.
     * @throws Exception
     */
    @Test
    public void testGetProductByIdThatNotExists() throws Exception {
        // Test getProductById with not exists product (with product id that not exists like productId=100).
        mvc.perform(get("/getProductById/{productId}", 6)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    /**
     * Test the method getProductById with product id that exists.
     * @throws Exception
     */
    @Test
    public void testGetProductByIdThatExists() throws Exception {

        // Test getProductById with an exists product.
        mvc.perform(get("/getProductById/{productId}", 1))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Transactional
    public void testAddNewProductWithValidProduct() throws Exception {

        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be null.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            mvc.perform(post("/admin/productAdd")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.CREATED.value()));
        }
    }

    @Test
    @Transactional
    public void testAddNewProductWithNegativePrice() throws Exception {

        // Sets negative price.
        product6.setPrice(-80.0);
        // Converts the Product Object to String.
        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/productAdd",
            // and expect the BAD_REQUEST error for add product with negative price.
            mvc.perform(post("/admin/productAdd")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    @Transactional
    public void testAddNewProductWithNullPrice() throws Exception {
        // Sets negative price.
        product6.setPrice(null);

        // Converts the Product Object to String.
        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/productAdd",
            // and expect the BAD_REQUEST error for add product with null price.
            mvc.perform(post("/admin/productAdd")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    @Transactional
    public void testAddNewProductWithNegativeProductQuantity() throws Exception {

        // Sets negative price.
        product6.setProductQuantity(-76);

        // Converts the Product Object to String.
        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/productAdd",
            // and expect the BAD_REQUEST error for add product with negative quantity.
            mvc.perform(post("/admin/productAdd")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    @Transactional
    public void testAddNewProductWithNullProductQuantity() throws Exception {

        // Sets negative price.
        product6.setProductQuantity(null);

        // Converts the Product Object to String.
        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/productAdd",
            // and expect the BAD_REQUEST error for add product with null quantity.
            mvc.perform(post("/admin/productAdd")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    @Transactional
    public void testAddNewProductWithNullProductName() throws Exception {

        // Sets negative price.
        product6.setName(null);

        // Converts the Product Object to String.
        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/productAdd",
            // and expect the BAD_REQUEST error for add product with null product name.
            mvc.perform(post("/admin/productAdd")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    @Transactional
    public void testAddNewProductWithBlankProductName() throws Exception {

        // Sets negative price.
        product6.setName("");

        // Converts the Product Object to String.
        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/productAdd",
            // and expect the BAD_REQUEST error for add product with blank product name.
            mvc.perform(post("/admin/productAdd")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    @Transactional
    public void testAddNewProductWithNullDescription() throws Exception {

        // Sets negative price.
        product6.setDescription(null);

        // Converts the Product Object to String.
        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/productAdd",
            // and expect the BAD_REQUEST error for add product with null description.
            mvc.perform(post("/admin/productAdd")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    @Transactional
    public void testAddNewProductWithBlankDescription() throws Exception{

        // Sets negative price.
        product6.setDescription("");

        // Converts the Product Object to String.
        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/productAdd",
            // and expect the BAD_REQUEST error for add product with blank description.
            mvc.perform(post("/admin/productAdd")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    @Transactional
    public void testUpdateProductWithUnMatchProductId() throws Exception {

        // Converts the Product Object to String.
        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/updateProduct/{productId}",
            // and expect the BAD_REQUEST error for update product with wrong product id.
            mvc.perform(patch("/admin/updateProduct/{productId}", 1L)
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    @Transactional
    public void testValidUpdateProduct() throws Exception {

        // Sets product6 id to productName: Product Test #1.
        product6.setProductId(1L);
        product6.setName("Product Test #1 Updated");
        product6.setDescription("Description of product test #1 updated.");
        product6.setPrice(11.0);
        product6.setProductQuantity(21);

        // Converts the Product Object to String.
        String productAddRequest = mapper.writeValueAsString(product6);

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/updateProduct/{productId}",
            // and expect the BAD_REQUEST error for update product with wrong product id.
            mvc.perform(patch("/admin/updateProduct/{productId}", 1L)
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(productAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.OK.value()));
        }
    }

    @Test
    @Transactional
    public void testDeleteProductWithNotExistsProductId() throws Exception {

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/deleteProductById/{productId}",
            // and expect the FORBIDDEN error for delete product with not exists product id.
            mvc.perform(delete("/admin/deleteProductById/{productId}", 6L)
                            .header("Authorization", "Bearer " + adminToken)
                    )
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

    @Test
    @Transactional
    public void testDeleteProductWithExistsProductId() throws Exception {

        // Checks if the testAdmin token is not null.
        Assertions.assertNotNull(adminToken, "The adminToken should not be.");

        if(jwtService.getUsernameClaim(adminToken).equals(testAdmin.getUsername())) {
            // Performs HTTP call 'post' on "/admin/deleteProductById/{productId}",
            // and expect the OK status for delete product with an exists product id.
            mvc.perform(delete("/admin/deleteProductById/{productId}", 4L)
                            .header("Authorization", "Bearer " + adminToken)
                    )
                    .andExpect(status().is(HttpStatus.OK.value()));
        }
    }
}
