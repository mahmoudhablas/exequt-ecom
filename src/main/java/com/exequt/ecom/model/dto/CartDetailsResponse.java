package com.exequt.ecom.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDetailsResponse {

    private Long cartId;

    private Long customerId;

    private String status;

    private List<CartItemDetailsResponse> items;

    private Integer totalItems;

    private BigDecimal subtotal;

    private BigDecimal totalPrice;

    private String currency;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

