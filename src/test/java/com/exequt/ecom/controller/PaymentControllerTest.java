package com.exequt.ecom.controller;

import com.exequt.ecom.model.dto.PaymentProviderCallback;
import com.exequt.ecom.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {
    @Mock
    private PaymentService paymentService;
    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handlePaymentWebhook_returnsNoContent() {
        PaymentProviderCallback callback = new PaymentProviderCallback();
        ResponseEntity<Void> response = paymentController.handlePaymentWebhook(callback, "corr-id");
        assertEquals(204, response.getStatusCode().value());
        verify(paymentService, times(1)).recieveCallback(callback);
    }
}

