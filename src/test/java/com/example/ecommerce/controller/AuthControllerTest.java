package com.example.ecommerce.controller;

import com.example.ecommerce.api_model.RegistrationBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** This class tests the endpoints of the AuthController class. */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    /** This instance mocks MVC, which allows performing HTTP calls. */
    @Autowired
    private MockMvc mvc;

    /** An instance of imaginary email server for register testing. */
    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig()
                    .withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    /**
     * Tests the registerUser endpoint.
     * @throws Exception
     */
    @Test
    @Transactional
    public void testRegisterUser() throws Exception {

        // This object mapper instance can convert an object to different types of formats like
        // convert a java object to JSON.
        ObjectMapper mapper = new ObjectMapper();

        // Creates the registration body for tests.
        RegistrationBody body = new RegistrationBody();

        // Tests registration with an invalid body registration.

        // Sets body registration with a null username.
        body.setUsername(null);
        body.setEmail("AuthControllerTest.testRegisterUser@junit.com");
        body.setFirstName("FirstName");
        body.setLastName("LastName");
        body.setPassword("Password$123");

        // Performs HTTP call 'post' on "/auth/register",
        // when the java object of registration body converted to JSON
        // and expect the BAD_REQUEST error for register with an invalid registration body.
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value())); //TODO: handle register errors like null username.

        // Sets body registration with a null email.
        body.setUsername("ProperUseName");
        body.setEmail(null);

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets body registration with a null first name.
        body.setEmail("AuthControllerTest.testRegisterUser@junit.com");
        body.setFirstName(null);

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets body registration with a null last name.
        body.setFirstName("FirstName");
        body.setLastName(null);

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets body registration with a null password.
        body.setLastName("LastName");
        body.setPassword(null);

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test again each data field in the body registration with blank

        // Sets body registration with blank username.
        body.setPassword("Password$123");
        body.setUsername("");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets body registration with a blank email.
        body.setUsername("ProperUseName");
        body.setEmail("");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets body registration with a blank first name.
        body.setEmail("AuthControllerTest.testRegisterUser@junit.com");
        body.setFirstName("");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets body registration with a blank last name.
        body.setFirstName("FirstName");
        body.setLastName("");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test register user again with blank password.
        body.setLastName("LastName");
        body.setPassword("");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Tests register user with a valid body registration.
        body.setPassword("Password$123");
        //body.setPassword("Password#123");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.OK.value()));

        // Test password characters

        // Sets password with less than 8 characters.
        body.setPassword("Pass1!");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets password with more than 64 characters.
        body.setPassword("PasswordPasswordPasswordPasswordPasswordPasswordPassword#123456789");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets password without upper case.
        body.setPassword("password$123");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets password without lower case.
        body.setPassword("PASSWORD$123");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets password without special character.
        body.setPassword("Password123");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets password without number.
        body.setPassword("Password$");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test username length

        // Sets username with length that under 6 (the minimum) characters.
        body.setPassword("Password$123"); // Sets a valid password.
        body.setUsername("Prop");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets username with length that more than 20 (the minimum) characters.
        body.setUsername("PropPropPropPropProper");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test email validity.

        //"AuthControllerTest.testRegisterUser@junit.com"

        // Sets email without the local part.
        body.setEmail("@junit.com");
        body.setUsername("ProperUseName"); // Sets a proper username.

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets email without the symbol @.
        body.setEmail("AuthControllerTest.testRegisterUser,junit.com");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Sets email without the domain part.
        body.setEmail("AuthControllerTest.testRegisterUser@.com");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Set a proper email.
        body.setEmail("AuthControllerTest.testRegisterUser@junit.com");

    }


}
