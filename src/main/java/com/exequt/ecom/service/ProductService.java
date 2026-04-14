package com.exequt.ecom.service;

import com.exequt.ecom.mapper.ProductMapper;
import com.exequt.ecom.model.ProductEntity;
import com.exequt.ecom.model.Product;
import com.exequt.ecom.model.ProductResponse;
import com.exequt.ecom.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

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
