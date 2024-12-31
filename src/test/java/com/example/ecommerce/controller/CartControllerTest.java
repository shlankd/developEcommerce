package com.example.ecommerce.controller;

import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.EcommUser;
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

/** This class tests the endpoints of the CartController class. */
@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

    /** This instance mocks MVC, which allows performing HTTP calls. */
    @Autowired
    private MockMvc mvc; // MVC: Model View Controller.

    @Autowired
    private EcommUserRepository ecommUserRepository;

    @Autowired
    private JWTService jwtService;

    private ObjectMapper mapper;
    private EcommUser testUser1;
    private String testUser1Token;
    private EcommUser testUser3;
    private String testUser3Token;

    @BeforeEach
    void setUp() {

        testUser1Token = null;

        mapper = new ObjectMapper();

        // Username: "testAdmin" from the data.sql file.
        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");

        if(opTestUser1.isPresent()) {

            testUser1 = opTestUser1.get();

            testUser1Token = jwtService.generateJWT(testUser1);
        }

        // Username: "testAdmin" from the data.sql file.
        Optional<EcommUser> opTestUser3 = ecommUserRepository.findByUsernameIgnoreCase("testUser3");

        if(opTestUser3.isPresent()) {

            testUser3 = opTestUser3.get();

            testUser3Token = jwtService.generateJWT(testUser3);
        }
    }

    @Test
    public void testGetAllCartItemsWithUserIdThatNotMatchWithAuthUser() throws Exception {

        Long testUser3Id = testUser3.getId();

        // Checks if the testUser1 token is not null.
        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");

        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {

            // Performs HTTP call to get all cart items of testUser1 with user id of testUser3.
            mvc.perform(get("/user/{userId}/getCartItems", testUser3Id)
                            .header("Authorization", "Bearer " + testUser1Token)
                    )
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

    @Test
    public void testGetAllCartItemsSuccessfully() throws Exception {

        Long testUser1Id = testUser1.getId();

        // Checks if the testUser1 token is not null.
        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");

        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {

            // Performs HTTP call to get all cart items of testUser1.
            mvc.perform(get("/user/{userId}/getCartItems", testUser1Id)
                            .header("Authorization", "Bearer " + testUser1Token)
                    )
                    .andExpect(status().is(HttpStatus.OK.value()))
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Test
    @Transactional
    public void testAddCartItemWithUserIdThatNotMatchWithAuthUser() throws Exception {

        // Sets the product id.
        Long productIdNotExists = 1L;

        // Sets user id with different id of the testUser1.
        Long testUser1IdInvalid = 3L;

        CartItem cartItemToAdd = new CartItem();

        cartItemToAdd.setCartItemQuantity(5);

        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);

        // Checks if the testUser1 token is not null.
        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");

        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
            mvc.perform(put("/user/{userId}/addItemToCart/{productId}", testUser1IdInvalid, productIdNotExists)
                            .header("Authorization", "Bearer " + testUser1Token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(cartItemAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

//    @Test
//    @Transactional
//    public void testAddItemToCartWithNotExistsProductId() throws Exception {
//
//        // Sets a non exists product id.
//        Long productIdNotExists = 5L;
//
//        // Gets the user id of testUser1.
//        Long testUser1Id = testUser1.getId();
//
//        CartItem cartItemToAdd = new CartItem();
//
//        cartItemToAdd.setCartItemQuantity(5);
//
//        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);
//
//        // Checks if the testUser1 token is not null.
//        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");
//
//        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
//            mvc.perform(put("/user/{userId}/addItemToCart/{productId}", testUser1Id, productIdNotExists)
//                            .header("Authorization", "Bearer " + testUser1Token)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(cartItemAddRequest)
//                    )
//                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
//        }
//    }

//    @Test
//    @Transactional
//    public void testAddItemToCartWithProductOutOfStock() throws Exception {
//
//        // From data.sql the product with id=4L is with 0 quantity.
//        Long productId = 4L;
//
//        // Gets the user id of testUser1.
//        Long testUser1Id = testUser1.getId();
//
//        CartItem cartItemToAdd = new CartItem();
//
//        cartItemToAdd.setCartItemQuantity(5);
//
//        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);
//
//        // Checks if the testUser1 token is not null.
//        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");
//
//        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
//            mvc.perform(put("/user/{userId}/addItemToCart/{productId}", testUser1Id, productId)
//                            .header("Authorization", "Bearer " + testUser1Token)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(cartItemAddRequest)
//                    )
//                    .andExpect(status().is(HttpStatus.CONFLICT.value()));
//        }
//    }

//    @Test
//    @Transactional
//    public void testAddItemToCartWithNotAvailableQuantity() throws Exception {
//
//        // From data.sql the product with id=1L has product quantity of 20.
//        Long productId = 1L;
//
//        // Gets the user id of testUser1.
//        Long testUser1Id = testUser1.getId();
//
//        CartItem cartItemToAdd = new CartItem();
//
//        // Sets CartItem quantity with 21.
//        cartItemToAdd.setCartItemQuantity(21);
//
//        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);
//
//        // Checks if the testUser1 token is not null.
//        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");
//
//        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
//            mvc.perform(put("/user/{userId}/addItemToCart/{productId}", testUser1Id, productId)
//                            .header("Authorization", "Bearer " + testUser1Token)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(cartItemAddRequest)
//                    )
//                    .andExpect(status().is(HttpStatus.CONFLICT.value()));
//        }
//    }

    @Test
    @Transactional
    public void testAddItemToCartSuccessfully() throws Exception {

        // From data.sql the product with id=1L which is with product name of 'Product Test #1'.
        Long productId = 1L;

        // Gets the user id of testUser1.
        Long testUser1Id = testUser1.getId();

        CartItem cartItemToAdd = new CartItem();

        cartItemToAdd.setCartItemQuantity(5);

        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);

        // Checks if the testUser1 token is not null.
        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");

        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
            mvc.perform(put("/user/{userId}/addItemToCart/{productId}", testUser1Id, productId)
                            .header("Authorization", "Bearer " + testUser1Token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(cartItemAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.OK.value()));
        }
    }

    @Test
    @Transactional
    public void testEditCartItemQuantityWithUserIdThatNotMatchWithAuthUser() throws Exception{

        Long cartItemId = 1L;

        // Sets user id with different id of the testUser1.
        Long testUser1IdInvalid = 3L;

        CartItem cartItemToAdd = new CartItem();

        cartItemToAdd.setCartItemId(cartItemId);
        cartItemToAdd.setCartItemQuantity(5);

        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);

        // Checks if the testUser1 token is not null.
        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");

        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
            mvc.perform(patch("/user/{userId}/editCartItemQuantity/{cartItemId}", testUser1IdInvalid, cartItemId)
                            .header("Authorization", "Bearer " + testUser1Token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(cartItemAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

//    @Test
//    @Transactional
//    public void testEditCartItemQuantityWithUserIdThatNotMatchWithTheUserThatTheCartItemIsBelong() throws Exception{
//
//        Long cartItemIdThatBelongsToTestUser1 = 1L;
//
//        // Sets user id with different id of the testUser1 like the id of the testUser3.
//        Long testUser3Id = testUser3.getId();
//
//        CartItem cartItemToAdd = new CartItem();
//
//        cartItemToAdd.setCartItemId(cartItemIdThatBelongsToTestUser1);
//        cartItemToAdd.setCartItemQuantity(5);
//
//        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);
//
//        // Checks if the testUser1 token is not null.
//        Assertions.assertNotNull(testUser3Token, "The testUser3Token should not be null.");
//
//        if(jwtService.getUsernameClaim(testUser3Token).equals(testUser3.getUsername())) {
//            mvc.perform(patch("/user/{userId}/editCartItemQuantity/{cartItemId}", testUser3Id, cartItemIdThatBelongsToTestUser1)
//                            .header("Authorization", "Bearer " + testUser3Token)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(cartItemAddRequest)
//                    )
//                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
//        }
//    }

//    @Test
//    @Transactional
//    public void testEditCartItemQuantityWithCartItemIdThatNotExists() throws Exception{
//
//        Long cartItemIdNotExists = 4L;
//
//        // Sets user id with different id of the testUser1 like the id of the testUser3.
//        Long testUser1Id = testUser1.getId();
//
//        CartItem cartItemToAdd = new CartItem();
//
//        cartItemToAdd.setCartItemId(cartItemIdNotExists);
//        cartItemToAdd.setCartItemQuantity(5);
//
//        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);
//
//        // Checks if the testUser1 token is not null.
//        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");
//
//        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
//            mvc.perform(patch("/user/{userId}/editCartItemQuantity/{cartItemId}", testUser1Id, cartItemIdNotExists)
//                            .header("Authorization", "Bearer " + testUser1Token)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(cartItemAddRequest)
//                    )
//                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
//        }
//    }

//    @Test
//    @Transactional
//    public void testEditCartItemQuantityWithUnMatchedCartItemId() throws Exception{
//
//        Long cartItemId = 1L;
//        Long cartItemToEditId = 2L;
//
//        // Sets user id with different id of the testUser1 like the id of the testUser3.
//        Long testUser1Id = testUser1.getId();
//
//        CartItem cartItemToAdd = new CartItem();
//
//        cartItemToAdd.setCartItemId(cartItemToEditId);
//        cartItemToAdd.setCartItemQuantity(7);
//
//        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);
//
//        // Checks if the testUser1 token is not null.
//        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");
//
//        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
//            mvc.perform(patch("/user/{userId}/editCartItemQuantity/{cartItemId}", testUser1Id, cartItemId)
//                            .header("Authorization", "Bearer " + testUser1Token)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(cartItemAddRequest)
//                    )
//                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
//        }
//    }

//    @Test
//    @Transactional
//    public void testEditCartItemQuantityWithNotAvailableQuantity() throws Exception{
//
//        // From data.sql the product with id=2L has product quantity of 30.
//        Long cartItemIdToEdit = 2L;
//
//        // Sets user id of the testUser1 that the cartItem id=2 belongs to.
//        Long testUser1Id = testUser1.getId();
//
//        CartItem cartItemToAdd = new CartItem();
//
//        cartItemToAdd.setCartItemId(cartItemIdToEdit);
//
//        // Sets the cart item quantity bigger than the product 'Product Test #2'.
//        cartItemToAdd.setCartItemQuantity(50);
//
//        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);
//
//        // Checks if the testUser1 token is not null.
//        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");
//
//        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
//            mvc.perform(patch("/user/{userId}/editCartItemQuantity/{cartItemId}", testUser1Id, cartItemIdToEdit)
//                            .header("Authorization", "Bearer " + testUser1Token)
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(cartItemAddRequest)
//                    )
//                    .andExpect(status().is(HttpStatus.CONFLICT.value()));
//        }
//    }

    @Test
    @Transactional
    public void testEditCartItemQuantitySuccessfully() throws Exception{

        // From data.sql the product with id=2L has product quantity of 30.
        Long cartItemIdToEdit = 2L;

        // Sets user id of the testUser1 that the cartItem id=2 belongs to.
        Long testUser1Id = testUser1.getId();

        CartItem cartItemToAdd = new CartItem();

        cartItemToAdd.setCartItemId(cartItemIdToEdit);

        // Sets the cart item quantity of the product 'Product Test #2'.
        cartItemToAdd.setCartItemQuantity(7);

        String cartItemAddRequest = mapper.writeValueAsString(cartItemToAdd);

        // Checks if the testUser1 token is not null.
        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");

        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
            mvc.perform(patch("/user/{userId}/editCartItemQuantity/{cartItemId}", testUser1Id, cartItemIdToEdit)
                            .header("Authorization", "Bearer " + testUser1Token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(cartItemAddRequest)
                    )
                    .andExpect(status().is(HttpStatus.OK.value()));
        }
    }

    @Test
    @Transactional
    public void testDeleteCartItemWithUserIdThatNotMatchWithAuthUser() throws Exception{

        Long cartItemId = 3L;

        // Sets user id of the testUser1 instead of the id of the testUser3.
        Long testUser3IdWrong = testUser1.getId();

        // Checks if the testUser1 token is not null.
        Assertions.assertNotNull(testUser3Token, "The testUser3Token should not be null.");

        if(jwtService.getUsernameClaim(testUser3Token).equals(testUser3.getUsername())) {
            mvc.perform(delete("/user/{userId}/deleteCartItem/{cartItemId}", testUser3IdWrong, cartItemId)
                            .header("Authorization", "Bearer " + testUser3Token)
                    )
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

//    @Test
//    @Transactional
//    public void testDeleteCartItemWithUserIdThatNotMatchWithTheUserThatTheCartItemIsBelong() throws Exception{
//
//        Long cartItemIdThatBelongsToTestUser3 = 3L;
//
//        // Sets user id  of the testUser1.
//        Long testUser1Id = testUser1.getId();
//
//        // Checks if the testUser1 token is not null.
//        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");
//
//        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
//            mvc.perform(delete("/user/{userId}/deleteCartItem/{cartItemId}", testUser1Id, cartItemIdThatBelongsToTestUser3)
//                            .header("Authorization", "Bearer " + testUser1Token)
//                    )
//                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
//        }
//    }

//    @Test
//    @Transactional
//    public void testDeleteCartItemWithCartItemIdNotExists() throws Exception{
//
//        Long cartItemIdNotExists = 4L;
//
//        // Sets user id  of the testUser1.
//        Long testUser1Id = testUser1.getId();
//
//        // Checks if the testUser1 token is not null.
//        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");
//
//        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
//            mvc.perform(delete("/user/{userId}/deleteCartItem/{cartItemId}", testUser1Id, cartItemIdNotExists)
//                            .header("Authorization", "Bearer " + testUser1Token)
//                    )
//                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
//        }
//    }

    @Test
    @Transactional
    public void testDeleteCartItemSuccessfully() throws Exception{

        Long cartItemIdToDelete= 1L;

        // Sets user id  of the testUser1 that has the cart item id=1L.
        Long testUser1Id = testUser1.getId();

        // Checks if the testUser1 token is not null.
        Assertions.assertNotNull(testUser1Token, "The testUser1Token should not be null.");

        if(jwtService.getUsernameClaim(testUser1Token).equals(testUser1.getUsername())) {
            mvc.perform(delete("/user/{userId}/deleteCartItem/{cartItemId}", testUser1Id, cartItemIdToDelete)
                            .header("Authorization", "Bearer " + testUser1Token)
                    )
                    .andExpect(status().is(HttpStatus.OK.value()));
        }
    }
}

