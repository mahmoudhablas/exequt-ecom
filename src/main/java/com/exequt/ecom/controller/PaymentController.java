package com.exequt.ecom.controller;

import com.exequt.ecom.model.dto.PaymentProviderCallback;
import com.exequt.ecom.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/v1/payments")
@AllArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> handlePaymentWebhook(
            @RequestBody PaymentProviderCallback paymentProviderCallback,
            @RequestHeader(value = "X-Correlation-Id") String correlationId) {
        log.info("[handlePaymentWebhook] Received payment webhook with correlationId={}, provider={}, paymentId={}, status={}",
                correlationId,
                paymentProviderCallback.getProvider(),
                paymentProviderCallback.getPaymentId(),
                paymentProviderCallback.getStatus()
        );
        this.paymentService.recieveCallback(paymentProviderCallback);
        log.info("[handlePaymentWebhook] Callback processed for provider={}, paymentId={}",
                paymentProviderCallback.getProvider(),
                paymentProviderCallback.getPaymentId()
        );
        return ResponseEntity.noContent().build();
    }

}
