package com.example.ecommerce.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * Service class for handling encryption of passwords.
 */
@Service
public class EncryptionService {

    /** How many salt rounds should the encryption run. */
    @Value("${encryption.salt.rounds}")
    private int saltRound;

    /** The salt built after construction. */
    private String salt;

    /** Post construction method. */
    @PostConstruct
    public void postConstruct(){
        salt = BCrypt.gensalt(saltRound);
    }

    /**
     * Encryption the given password.
     * @param password the given text word password.
     * @return The encrypted password.
     */
    public String encryptPassword(String password){
        return BCrypt.hashpw(password, salt);
    }

    /**
     * Checks the validation of the password.
     * @param password the given text word password.
     * @param hash The encrypted password.
     * @return True if the password is correct, false otherwise.
     */
    public boolean verifyPassword(String password, String hash){
        return BCrypt.checkpw(password, hash);
    }
}
