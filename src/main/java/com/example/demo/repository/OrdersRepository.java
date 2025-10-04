package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Orders;

/**
 * Repository interface for managing {@link Orders} entities.
 * Provides CRUD operations and query methods via Spring Data JPA.
 */
public interface OrdersRepository extends JpaRepository<Orders,Long> {

}
