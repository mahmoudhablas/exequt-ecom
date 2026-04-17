package com.exequt.ecom.service;

import com.exequt.ecom.exception.ProductNotFoundException;
import com.exequt.ecom.exception.ProductOutOfStockException;
import com.exequt.ecom.mapper.ProductMapper;
import com.exequt.ecom.model.entity.ProductEntity;
import com.exequt.ecom.model.dto.Product;
import com.exequt.ecom.model.dto.ProductResponse;
import com.exequt.ecom.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductEntity getAvailableProduct (Integer productId) {
        log.debug("Validating product availability: productId={}", productId);
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (product.getNumberInStock() <= 0) {
            log.warn("Product out of stock: productId={}", productId);
            throw new ProductOutOfStockException(productId);
        }
        return product;
    }
    public List<ProductEntity> getAvailableProducts(List<Integer> ids) {
        List<ProductEntity> products = productRepository.findAllById(ids);
        if (products.size() != ids.size()) {
            Set<Integer> found = products.stream()
                    .map(ProductEntity::getId).collect(Collectors.toSet());
            ids.stream().filter(id -> !found.contains(id))
                    .findFirst()
                    .ifPresent(id -> { throw new ProductNotFoundException(id); });
        }
        if(products.stream().anyMatch(p -> p.getNumberInStock() <= 0)) {
            Integer outOfStockId = products.stream()
                    .filter(p -> p.getNumberInStock() <= 0)
                    .map(ProductEntity::getId)
                    .findFirst()
                    .orElseThrow();
            log.warn("Product out of stock: productId={}", outOfStockId);
            throw new ProductOutOfStockException(outOfStockId);
        }
        return products;
    }
    public ProductResponse getProducts(Integer cursor, int limit) {


        List<ProductEntity> products = productRepository.findNextPage(cursor,limit+1);

        boolean hasNext = products.size() > limit;

        if (hasNext) {
            products = products.subList(0, limit);
        }

        List<Product> response = this.productMapper.mapToProductResponses(products);

        Integer nextCursor = response.isEmpty()
                ? null
                : response.get(response.size() - 1).getId();

        return this.productMapper.mapToProductResponse(response, nextCursor, hasNext);
    }

}
