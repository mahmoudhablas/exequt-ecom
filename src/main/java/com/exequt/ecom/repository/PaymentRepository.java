package com.exequt.ecom.repository;

import com.exequt.ecom.model.entity.PaymentEntity;
import com.exequt.ecom.model.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByOrderId(Long orderId);

    // Idempotency check — avoid duplicate payments for same provider transaction
    Optional<PaymentEntity> findByProviderAndProviderRef(String provider, String providerRef);

    boolean existsByProviderAndProviderRef(String provider, String providerRef);

    List<PaymentEntity> findByStatus(PaymentStatus status);
}