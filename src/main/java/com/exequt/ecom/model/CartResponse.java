package com.exequt.ecom.model;

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
public class CartResponse
{
    private Long cartId;

//    private Long customerId;
//
//    private String status;
//
//    private List<CartItemResponse> items;
//
//    private BigDecimal totalPrice;
//
//    private String currency;
//
//    private Integer totalItems;
//
//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;
}
