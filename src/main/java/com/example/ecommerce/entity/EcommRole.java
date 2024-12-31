package com.example.ecommerce.entity;

import jakarta.persistence.*;

/**
 * This EcommRole class defines different kinds of access permissions in the e-commerce.
 */
@Entity
@Table(name = "ecomm_role")
public class EcommRole {

    /** role id. */
    @Id
    private Long id;

    /** EcommRole's name (unique). */
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    // Getters and Setters.

    /**
     * Gets the role id.
     * @return Returns The role id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the role's name.
     * @param id The role id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the role's name.
     * @return Returns The role's name.
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Sets the role's name.
     * @param roleName The role's name to set.
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}
