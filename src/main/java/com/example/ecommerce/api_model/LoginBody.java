package com.example.ecommerce.api_model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * The body set for the user to login.
 */
public class LoginBody {

    /** The username for user's login. */
    @NotNull
    @NotBlank
    private String username;

    /** The password for user's login. */
    @NotNull
    @NotBlank
    private String password;

    /** Getters and Setters. */

    public @NotNull @NotBlank String getUsername() {
        return username;
    }

    public void setUsername(@NotNull @NotBlank String username) {
        this.username = username;
    }

    public @NotNull @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @NotBlank String password) {
        this.password = password;
    }
}
