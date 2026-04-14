package com.exequt.ecom.repository;

import com.exequt.ecom.model.CartEntity;
import com.exequt.ecom.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
