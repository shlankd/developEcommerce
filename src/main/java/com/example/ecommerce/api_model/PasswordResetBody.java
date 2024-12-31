package com.example.ecommerce.api_model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * This class contains token and password instances for reset password process.
 */
public class PasswordResetBody {

    /** Token for authentication. */
    @NotNull
    @NotBlank
    private String token;

    /** Password to set to the user's account. */
    @NotNull
    @NotBlank
    @Size(min=8, max = 64)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private String password;

    /** Getters and Setters. */

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
