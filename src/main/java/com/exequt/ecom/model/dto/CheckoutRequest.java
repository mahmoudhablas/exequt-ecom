package com.exequt.ecom.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CheckoutRequest {
    private String shappingAddress;
    private String billingAddress;
}
