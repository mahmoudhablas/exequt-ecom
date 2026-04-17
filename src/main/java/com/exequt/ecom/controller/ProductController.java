package com.exequt.ecom.controller;

import com.exequt.ecom.model.dto.ProductResponse;
import com.exequt.ecom.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("v1/products")
@Slf4j
public class ProductController {

      private final ProductService productService;

      public ProductController(ProductService productService) {
          this.productService = productService;
      }

    @GetMapping
    public ResponseEntity<ProductResponse> getProducts(
            @RequestHeader("X-Correlation-Id") String correlationId,
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "10") int limit
                ) {
        log.info("[getProducts] correlationId={}, cursor={}, limit={}", correlationId, cursor, limit);
        if (limit <= 0 || limit > 100) {
            log.warn("[getProducts] Invalid limit: {}", limit);
            return ResponseEntity.badRequest().build();
        }

        ProductResponse response = productService.getProducts(cursor, limit);
        log.info("[getProducts] Returned {} products", response.getProducts().size());
        return ResponseEntity.ok(response);
    }


}
