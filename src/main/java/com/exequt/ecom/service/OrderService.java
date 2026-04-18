package com.exequt.ecom.service;

import com.exequt.ecom.exception.OrderNotFoundException;
import com.exequt.ecom.model.entity.*;
import com.exequt.ecom.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

import static ch.qos.logback.classic.spi.ThrowableProxyVO.build;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void cancelOrder(Long orderId) {

        OrderEntity order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if(order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        if(order.getStatus() == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Completed order cannot be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        this.orderRepository.save(order);
    }
    public OrderEntity createFromCart(CartEntity cart) {

        OrderEntity order = OrderEntity.builder()
                .customer(cart.getCustomer())
                .cart(cart)
                .status(OrderStatus.CREATED)
                .subtotal(calculateTotal(cart))
                .total(calculateTotal(cart))
                .items(new ArrayList<>())
                .tax(BigDecimal.ZERO)
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
