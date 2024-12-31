package com.example.ecommerce.repository;

import com.example.ecommerce.entity.EcommOrder;
import com.example.ecommerce.entity.EcommUser;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

/**
 * An interface class to create data access for objects of EcommOrder data.
 */
public interface EcommOrderRepository extends ListCrudRepository<EcommOrder, Long> {

    List<EcommOrder> findByUser(EcommUser user);
}
