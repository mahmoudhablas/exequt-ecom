package com.exequt.ecom.service;

import com.exequt.ecom.exception.OrderNotFoundException;
import com.exequt.ecom.model.dto.PaymentRequest;
import com.exequt.ecom.model.entity.OrderEntity;
import com.exequt.ecom.model.entity.OrderStatus;
import com.exequt.ecom.model.entity.PaymentEntity;
import com.exequt.ecom.model.entity.PaymentStatus;
import com.exequt.ecom.repository.OrderRepository;
import com.exequt.ecom.repository.PaymentRepository;
import com.exequt.ecom.client.PaymentProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.net.http.HttpConnectTimeoutException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentProvider paymentProvider;
    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void startProcessPayment_orderNotFound_throws() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> paymentService.startProcessPayment(1L, new PaymentRequest()));
    }

    @Test
    void startProcessPayment_success() throws HttpConnectTimeoutException {
        OrderEntity order = OrderEntity.builder().id(1L).status(OrderStatus.PENDING_PAYMENT).total(BigDecimal.TEN).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        PaymentEntity payment = PaymentEntity.builder().id(2L).order(order).status(PaymentStatus.PENDING).amount(BigDecimal.TEN).build();
        when(paymentRepository.save(any())).thenReturn(payment);
        doNothing().when(paymentProvider).processPayment(any());
        assertDoesNotThrow(() -> paymentService.startProcessPayment(1L, new PaymentRequest()));
    }
}

