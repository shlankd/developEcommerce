package com.example.ecommerce.api_model;

import jakarta.validation.constraints.*;

/**
 * The information that needs to fill for register as a user.
 */
public class RegistrationBody {

    /** The registered username. */
    @NotNull
    @NotBlank
    @Size(min=6, max = 20)
    private String username;

    /** The registered password. */
    @NotNull
    @NotBlank
    @Size(min=8, max = 64)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    // regexp = Minimum eight characters, at least one uppercase letter, one lowercase letter,
    //          one number and one special character
    private String password;

    /** The registered email. */
    @NotNull
    @NotBlank
    @Email
    private String email;

    /** The registered first name. */
    @NotNull
    @NotBlank
    private String firstName;

    /** The registered last name. */
    @NotNull
    @NotBlank
    private String lastName;

    /** Getters and Setters. */

    public @NotNull @NotBlank @Size(min = 6, max = 20) String getUsername() {
        return username;
    }

    public void setUsername(@NotNull @NotBlank @Size(min = 6, max = 20) String username) {
        this.username = username;
    }

    public @NotNull @NotBlank @Size(min = 8, max = 64) String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @NotBlank @Size(min = 8, max = 64) String password) {
        this.password = password;
    }

    public @NotNull @NotBlank @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotNull @NotBlank @Email String email) {
        this.email = email;
    }

    public @NotNull @NotBlank String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotNull @NotBlank String firstName) {
        this.firstName = firstName;
    }

    public @NotNull @NotBlank String getLastName() {
        return lastName;
    }

    public void setLastName(@NotNull @NotBlank String lastName) {
        this.lastName = lastName;
    }
}
