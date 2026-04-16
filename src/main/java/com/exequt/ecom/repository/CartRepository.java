package com.exequt.ecom.repository;

import com.exequt.ecom.model.CartEntity;
import com.exequt.ecom.model.CartStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Integer> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<CartEntity> findByCustomerIdAndStatus(Long customerId, CartStatus status);
    Optional<CartEntity> findWithLockById(Long cartId);

    Optional<CartEntity> findById(Long cartId);
}
