package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Address;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * An interface class to create data access for objects of Address data.
 */
public interface AddressRepository extends ListCrudRepository<Address, Long> {
    // Finds addresses by the user's id.
    List<Address> findByUser_Id(Long id);

    //Optional<Address> findByUser_IdAndAddress_Id(Long userId, Long addressId);

}
