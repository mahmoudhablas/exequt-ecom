package com.exequt.ecom.repository;

import com.exequt.ecom.model.ProductEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    @Query(value = """
    SELECT * FROM products
    WHERE (:cursor IS NULL OR id > :cursor)
    ORDER BY id ASC
    LIMIT :limit
    """, nativeQuery = true)
    List<ProductEntity> findNextPage(@Param("cursor") Integer cursor, @Param("limit") int limit);

}
