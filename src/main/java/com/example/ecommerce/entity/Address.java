package com.example.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/** Address for delivery products to the user. */
@Entity
@Table(name = "address")
public class Address {

    /** The id for the address (to make unique). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** Line of the address. */
    @Column(name = "address_line", nullable = false)
    private String addressLine;

    /** The city of the address. */
    @Column(name = "city", nullable = false)
    private String city;

    /** The city of the address. */
    @Column(name = "country", nullable = false, length = 80)
    private String country;


    /** The postcode of the address. */
    @Column(name = "postcode", nullable = false)
    private Integer postcode;

    /** Associate the address with the user. */
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private EcommUser user;

    // Getters and Setters.

    /**
     * Gets the user entity.
     * @return Returns the user entity of the address.
     */
    public EcommUser getUser() {
        return user;
    }

    /**
     * Sets the user entity of the address.
     * @param user The user entity of the address to set.
     */
    public void setUser(EcommUser user) {
        this.user = user;
    }

    /**
     * Gets the postcode of the address.
     * @return Returns the postcode of the address.
     */
    public Integer getPostcode() {
        return postcode;
    }

    /**
     * Sets the postcode of the address.
     * @param postcode The postcode of the address to set.
     */
    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }

    /**
     * Gets the country of the address.
     * @return Returns the country of the address.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country of the address.
     * @param country The country of the address to sets.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the city of the address.
     * @return Returns the city of the address.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the address.
     * @param city The city of the address to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the line address.
     * @return Returns address line.
     */
    public String getAddressLine() {
        return addressLine;
    }

    /**
     * Sets the line address.
     * @param addressLine The line address to set.
     */
    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    /**
     * Gets the address id.
     * @return Returns the address id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the address id.
     * @param id The address id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

}