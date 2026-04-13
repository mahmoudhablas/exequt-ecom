package com.exequt.ecom.controller;

import com.exequt.ecom.model.Product;
import com.exequt.ecom.model.ProductResponse;
import com.exequt.ecom.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;

@RestController
@RequestMapping("v1/products")
public class ProductController {

      private final ProductService productService;

      public ProductController(ProductService productService) {
          this.productService = productService;
      }

    @GetMapping
    public ResponseEntity<ProductResponse> getProducts(
            @RequestParam(required = false) Integer cursor,
            @RequestParam(defaultValue = "10") int limit
                ) {
        if (limit <= 0 || limit > 100) {
            return ResponseEntity.badRequest().build();
        }

        ProductResponse response = productService.getProducts(cursor, limit);
        return ResponseEntity.ok(response);
    }


}
