package com.exequt.ecom.controller;

import com.exequt.ecom.model.dto.ProductResponse;
import com.exequt.ecom.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {
    @Mock
    private ProductService productService;
    @InjectMocks
    private ProductController productController;

    public ProductControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProducts_returnsOk() {
        ProductResponse response = new ProductResponse();
        response.setProducts(Collections.emptyList());
        when(productService.getProducts(null, 10)).thenReturn(response);
        ResponseEntity<ProductResponse> result = productController.getProducts("corr-id", null, 10);
        assertEquals(200, result.getStatusCode().value());
        assertEquals(response, result.getBody());
    }

    @Test
    void getProducts_invalidLimit_returnsBadRequest() {
        ResponseEntity<ProductResponse> result = productController.getProducts("corr-id", null, 0);
        assertEquals(400, result.getStatusCode().value());
    }
}

