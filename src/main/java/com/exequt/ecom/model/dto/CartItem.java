package com.exequt.ecom.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CartItem {
    private Integer productId;

    private Integer quantity;
}
