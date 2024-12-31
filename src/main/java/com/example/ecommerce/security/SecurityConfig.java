package com.example.ecommerce.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

/** Configuration of the security. */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private JWTRequestFilter jwtRequestFilter;
    private JWTAuthEntryPoint jwtAuthEntryPoint;

    /**
     * SecurityConfig Constructor.
     * @param jwtRequestFilter The JWTRequestFilter object.
//     * @param jwtAuthEntryPoint The JWTAuthEntryPoint object.
     */
    public SecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
    }

    /**
     * This method configures the security of the HTTP request calls security.
     * @param http The security object.
     * @return The chain built.
     * @throws Exception Thrown when there is an error.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(csrf->csrf.disable()).cors(cors->cors.disable());

        // Sets what Http requests are authorize.
        http.authorizeHttpRequests(auth -> auth

                // Sets Http requests that don't have to be authenticated.
                .requestMatchers("/getAllProducts","/getProductById/{productId}", "/auth/register",
                        "/auth/login", "auth/verify","/auth/forgot", "/auth/reset","/error",
                        //"/websocket", "/websocket/**")
                        // remove the permit all of "/payment/**" when there is an end point that store jwt.
                        "/payment", "/payment/create","/payment/success", "/payment/cancel/{userId}/{orderId}", "/payment/error")
                .permitAll()

                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/user/**").hasAnyAuthority("ADMIN", "USER")

                // Everything else should be authenticated.
                .anyRequest().authenticated()
        );

        // Sets the configuration of http exception handling by jwtAuthEntryPoint.
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(jwtAuthEntryPoint)
        );

        // Sets the session management.
        http.sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );


        // Add the JWT request filter before the http request authentication filter.
        http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);

        return http.build();
    }


    /**
     *  Generates AuthenticationManager.
     * @param authenticationConfiguration The configuration of the AuthenticationManager to be created.
     * @return Returns the AuthenticationManager with the AuthenticationConfiguration.
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
