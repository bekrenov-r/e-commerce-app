package com.bekrenovr.ecommerce.orders.repository;

import com.bekrenovr.ecommerce.orders.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByCustomerEmail(String email);
}
