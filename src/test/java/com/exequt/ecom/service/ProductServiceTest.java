package com.exequt.ecom.service;

import com.exequt.ecom.mapper.ProductMapper;
import com.exequt.ecom.model.dto.Product;
import com.exequt.ecom.model.dto.ProductResponse;
import com.exequt.ecom.model.entity.ProductEntity;
import com.exequt.ecom.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductMapper productMapper;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProducts_returnsResponse() {
        List<ProductEntity> products = new ArrayList<>();
        products.add(
                ProductEntity.builder()
                        .id(1)
                        .name("Product 1")
                        .description("Description 1")
                        .price(BigDecimal.ONE)
                        .build()
        );
        when(productRepository.findNextPage(1,2)).thenReturn(products);
        List<Product> productList = new ArrayList<>();
        productList.add(
                Product.builder()
                        .id(1)
                        .name("Product 1")
                        .price(BigDecimal.ONE)
                        .build()
        );
        ProductResponse productResponse = ProductResponse.builder().products(productList).build();
        when(productMapper.mapToProductResponses(products)).thenReturn(productList);
        when(productMapper.mapToProductResponse(productList, 1,false)).thenReturn(productResponse);
        ProductResponse response = productService.getProducts(1, 1);
        assertNotNull(response);
    }
}

