package com.example.ecommerce.repository;

import com.example.ecommerce.entity.EcommUser;
import com.example.ecommerce.entity.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * An interface class to create data access for objects of VerificationToken data.
 */
public interface VerificationTokenRepository extends ListCrudRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(EcommUser user);

    List<VerificationToken> findByUser_IdOrderByIdDesc(Long id);
}
