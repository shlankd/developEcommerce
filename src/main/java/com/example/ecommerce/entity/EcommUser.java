package com.example.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

/** User account entity. */
@Entity
@Table(name = "ecomm_user")
public class EcommUser implements UserDetails {

    /** User's id (unique). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /** The user's name for his account (unique). */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /** User's password for the account. */
    @JsonIgnore
    @Column(name = "password", nullable = false, length = 1000)
    private String password;

    /** User's email. */
    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    /** User's first name. */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /** User's last name. */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /** A boolean instance that indicates if the user has verified email or not. */
    @Column(name = "verified_email", nullable = false)
    private Boolean verifiedEmail = false;

    /** List of the user's addresses that associate the user's entity with the address entity. */
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    /** List of verification tokens that have been sent to the user. */
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id desc") // Gives the latest verification token that been build because of the increment order of the id.
    private List<VerificationToken> verificationTokens = new ArrayList<>();

    /** The role of the user is an instance that indicate access authorization.
     * One user can have more than one role and one role can have many users. */
    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name ="user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<EcommRole> roles = new HashSet<>();

    /** The user's cart. */
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    // Getters and Setters.

    /**
     * Gets the list items from the user's cart.
     * @return Returns the cart items list.
     */
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    /**
     * Sets the user's cart items list.
     * @param cartItems The cart items list to set.
     */
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    /**
     * Checks if the user has verified email.
     * @return Returns the verifiedEmail instance that indicates true if the user have verified email,
     *         otherwise false.
     */
    public Boolean isVerifiedEmail() {
        return verifiedEmail;
    }

    /**
     * Sets the verifiedEmail instance.
     * @param verifiedEmail The verifiedEmail instance to set.
     */
    public void setVerifiedEmail(Boolean verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }

    /**
     * Gets the list of verification tokens that have been sent to the user.
     * @return Returns the verificationTokens instance.
     */
    public List<VerificationToken> getVerificationTokens() {
        return verificationTokens;
    }

    /**
     * Sets the list of verification tokens that have been sent to the user.
     * @param verificationTokens The verificationTokens to set.
     */
    public void setVerificationTokens(List<VerificationToken> verificationTokens) {
        this.verificationTokens = verificationTokens;
    }

    /**
     * Gets the user's list of his addresses.
     * @return Returns addresses.
     */
    public List<Address> getAddresses() {
        return addresses;
    }

    /**
     * Sets the user's list of his addresses.
     * @param addresses The list of user's addresses to set.
     */
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    /**
     * Gets the user's last name.
     * @return Returns user's last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     * @param lastName The user's last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the user's first name.
     * @return Returns user's first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     * @param firstName The user's first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the user's email.
     * @return Returns user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     * @param email The user's email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's password.
     * @return Returns user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     * @param password The user's password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the username.
     * @return username Returns username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the user's id.
     * @return Returns the user's id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user's id.
     * @param id The user's id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user's role.
     * @return Returns the user's role.
     */
    public Set<EcommRole> getRoles() {
        return roles;
    }

    /**
     * Sets the user's role
     * @param roles The user's roles to set.
     */
    public void setRoles(Set<EcommRole> roles) {
        this.roles = roles;
    }

    /**
     * Converts the roles into mapped granted authorities.
     * @return Returns a list of mapped authorities.
     */
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // For each role converts into authorities and insert into the authorities list and return.
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }


    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @JsonIgnore
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}