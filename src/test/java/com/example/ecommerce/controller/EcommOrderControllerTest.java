package com.example.ecommerce.controller;

import com.example.ecommerce.entity.EcommOrder;
import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.service.JWTService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** This class tests the endpoints of the OrderController class. */
@SpringBootTest
@AutoConfigureMockMvc
public class EcommOrderControllerTest {

    /** This instance mocks MVC, which allows performing HTTP calls. */
    @Autowired
    private MockMvc mvc;

    @Autowired
    private EcommUserRepository ecommUserRepository;

    @Autowired
    private JWTService jwtService;

    /**
     * This test checks if the unauthenticated user does not get the order list data.
     * @throws Exception
     */
    @Test
    public void testUnAuthenticatedOrderList() throws Exception {

        Optional<EcommUser> opTestUser1 = ecommUserRepository.findByUsernameIgnoreCase("testUser1");

        Assertions.assertTrue(opTestUser1.isPresent(), "The username testUser1 should be exists.");

        Long testUser1Id = opTestUser1.get().getId();

        mvc.perform(get("/user/{userId}/getOrders", testUser1Id)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    /**
     * This test checks if the testUser1 from data.sql file got his own order list.
     * @throws Exception
     */
    @Test
    @WithUserDetails("testUser1")
    public void testGetOrdersFromAuthenticatedUser1() throws Exception {
        testGetOrdersFromAuthenticatedUser("testUser1");
    }

    /**
     * This test checks if the testUser3 from data.sql file got his own order list.
     * @throws Exception
     */
    @Test
    @WithUserDetails("testUser2")
    public void testGetOrdersFromAuthenticatedUser2() throws Exception {
        testGetOrdersFromAuthenticatedUser("testUser3");
    }

    /**
     * Tests the get order list endpoint from a given authenticated user
     * and checks the order list belongs to the user that requested the order list.
     * @param username the username to test for (like 'testUser1' or 'testUser3' from data.sql file).
     * @throws Exception
     */
    private void testGetOrdersFromAuthenticatedUser(String username) throws Exception {

        Optional<EcommUser> opTestUser = ecommUserRepository.findByUsernameIgnoreCase(username);

        Assertions.assertTrue(opTestUser.isPresent(), "The username should be exists.");

        EcommUser testUser = opTestUser.get();
        Long testUserId = testUser.getId();
        String testUserToken = jwtService.generateJWT(testUser);

        mvc.perform(get("/user/{userId}/getOrders", testUserId)
                        .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    List<EcommOrder> orders = new ObjectMapper()
                            .readValue(json, new TypeReference<List<EcommOrder>>(){});
                    for(EcommOrder order : orders) {
                        Assertions.assertEquals(username, order.getUser().getUsername(),
                                "This order list should be belong to the user: " + username + ".");
                    }
                });
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
}
