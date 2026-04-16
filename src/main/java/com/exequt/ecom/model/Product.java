package com.exequt.ecom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer id;

    private String name;

    private String category;

    private BigDecimal price;

    private String currency;

    private Integer numberInStock;
}
