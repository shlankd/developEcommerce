package com.example.ecommerce.repository;

import com.example.ecommerce.entity.EcommUser;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

/**
 * An interface class to create data access for objects of EcommUser data.
 * A repository of data of users of ecommerce that gives access to the data of EcommUser objects.
 */
public interface EcommUserRepository extends ListCrudRepository<EcommUser, Long> {

    Optional<EcommUser> findByUsernameIgnoreCase(String username);

    Optional<EcommUser> findByEmailIgnoreCase(String email);
}
