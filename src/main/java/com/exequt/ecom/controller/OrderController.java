package com.exequt.ecom.controller;

import com.exequt.ecom.model.dto.PaymentProviderCallback;
import com.exequt.ecom.model.dto.PaymentRequest;
import com.exequt.ecom.service.OrderService;
import com.exequt.ecom.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/orders")
@AllArgsConstructor
@Slf4j
public class OrderController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @PostMapping("/{orderId}/paymment/start")
    public ResponseEntity<Void> startPaymentProcess(
            @RequestHeader("X-Correlation-Id") String correlationId,
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long orderId,
            @RequestBody PaymentRequest paymentRequest
            ) {
        this.paymentService.startProcessPayment(orderId, paymentRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @RequestHeader("X-Correlation-Id") String correlationId,
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long orderId
    ) {
        this.orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

}
