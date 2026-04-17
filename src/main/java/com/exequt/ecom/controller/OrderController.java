package com.exequt.ecom.controller;

import com.exequt.ecom.model.dto.PaymentProviderCallback;
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

    @PostMapping("/{orderId}/paymment/start}")
    public ResponseEntity<Void> startPaymentProcess(
            @RequestHeader("X-Correlation-Id") String correlationId,
            @RequestBody PaymentProviderCallback paymentProviderCallback
    ) {
        log.info("[startPaymentProcess] orderId={}, correlationId={}", paymentProviderCallback.getPaymentId(), correlationId);
        this.paymentService.recieveCallback(paymentProviderCallback);
        log.info("[startPaymentProcess] Payment process started for orderId={}", paymentProviderCallback.getPaymentId());
        return ResponseEntity.noContent().build();
    }

}
