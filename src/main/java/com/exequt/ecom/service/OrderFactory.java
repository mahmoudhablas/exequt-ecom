package com.exequt.ecom.service;

import com.exequt.ecom.model.entity.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
public class OrderFactory {

    public OrderEntity createFromCart(CartEntity cart) {

        OrderEntity order = OrderEntity.builder()
                .customer(cart.getCustomer())
                .cart(cart)
                .status(OrderStatus.CREATED)
                .subtotal(calculateTotal(cart))
                .total(calculateTotal(cart))
                .items(new ArrayList<>())
                .build();

        for (CartItemEntity item : cart.getItems()) {
            order.getItems().add(createOrderItem(order, item));
        }

        return order;
    }

    private BigDecimal calculateTotal(CartEntity cart) {
        return cart.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItemEntity createOrderItem(OrderEntity order, CartItemEntity item) {
        return OrderItemEntity.builder()
                .order(order)
                .product(item.getProduct())
                .productName(item.getProduct().getName())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}
