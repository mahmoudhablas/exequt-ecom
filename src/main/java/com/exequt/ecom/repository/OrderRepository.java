package com.exequt.ecom.repository;

import com.exequt.ecom.model.entity.OrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    // Single order scoped to customer — prevents accessing other customers' orders
    Optional<OrderEntity> findByIdAndCustomerId(Long orderId, Long customerId);


    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<OrderEntity> findById(Long orderId);
}