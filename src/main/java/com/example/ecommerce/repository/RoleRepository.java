package com.example.ecommerce.repository;

import com.example.ecommerce.entity.EcommRole;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

/**
 * An interface class to create data access for objects of EcommRole data.
 */
public interface RoleRepository extends ListCrudRepository<EcommRole, Long> {
    Optional<EcommRole> findByRoleName(String roleName);
}
