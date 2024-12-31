package com.example.ecommerce.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * Entity of verification token that is sent to the user's email.
 */
@Entity
@Table(name = "verification_token")
public class VerificationToken {

    /** The id of verification token. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** The verification token. */
    @Lob
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    /** The generated time stamp of the verification token. */
    @Column(name = "generated_timestamp", nullable = false)
    private Timestamp generatedTimestamp;

    /** The user that the verification token is sent to. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private EcommUser user;

    // Getters and Setters.

    public EcommUser getUser() {
        return user;
    }

    public void setUser(EcommUser user) {
        this.user = user;
    }

    public void setGeneratedTimestamp(Timestamp generatedTimestamp){
        this.generatedTimestamp = generatedTimestamp;
    }

    public Timestamp getGeneratedTimestamp() {
        return generatedTimestamp;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}