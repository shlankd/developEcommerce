package com.example.ecommerce.security;

import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.repository.EcommUserRepository;
import com.example.ecommerce.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This class tests the JWTRequestFilter class.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class JWTRequestFilterTest {

    /** This instance mocks MVC, which allows performing HTTP calls. */
    @Autowired
    private MockMvc mvc;

    /** A JWTService instance. */
    @Autowired
    private JWTService jwtService;

    /** A repository of users un ecommerce instance. */
    @Autowired
    private EcommUserRepository ecommUserRepository;

    /** The path of authenticated users. */
    private static final String AUTH_PATH = "/auth/me";

    /**
     * Tests unauthenticated requests that should be rejected.
     * @throws Exception
     */
    @Test
    public void testUnauthRequest() throws Exception {
        mvc.perform(get(AUTH_PATH)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    /**
     * Tests a null authentication requests.
     * @throws Exception
     */
    @Test
    public void testNullAuth() throws Exception {

        // Tests null token.
        mvc.perform(get(AUTH_PATH).header("Authorization", "NullToken"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        // Tests null token that starts with "Bearer ".
        mvc.perform(get(AUTH_PATH).header("Authorization", "Bearer NullToken"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    /**
     * Tests not verified user that has jwt.
     * @throws Exception
     */
    @Test
    public void testNotVerifiedUser() throws Exception {

        // Gets testUser2 that his verified_email is false (from data.sql file).
        EcommUser user = ecommUserRepository.findByUsernameIgnoreCase("testUser2").get();

        // Generates jwt to testUser2.
        String token = jwtService.generateJWT(user);

        // Tests the token from not verified user.
        mvc.perform(get(AUTH_PATH).header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    /**
     * Tests a valid authentication request.
     * @throws Exception
     */
    @Test
    public void testValidAuth() throws Exception {

        // Gets user1 that his verified_email is true (from data.sql file).
        EcommUser user = ecommUserRepository.findByUsernameIgnoreCase("testUser1").get();

//        EcommRole userRole = null;
//        Set<EcommRole> setRoles = user.getRoles();
//        for (EcommRole role : setRoles)
//        {
//            if (role.getRoleName().equals(new String("USER")))
//            {
//                userRole = role;
//            }
//        }
//        //Assertions.assertSame("3", three);
//
//        if(userRole != null) {
//            System.out.println("testUser1 role: " + userRole.getRoleName() + "\n");
//        }
//        else{
//            System.out.println("testUser1 role: empty\n");
//        }


        // Generates jwt to testUserser1.
        String token = jwtService.generateJWT(user);

        // Tests the token from a verified user that should be valid.
        mvc.perform(get(AUTH_PATH).header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
