package com.exequt.ecom.service;

import com.exequt.ecom.client.PaymentProvider;
import com.exequt.ecom.exception.InvalidOrderStateException;
import com.exequt.ecom.exception.OrderNotFoundException;
import com.exequt.ecom.exception.PaymentFailureException;
import com.exequt.ecom.exception.PaymentNotFoundException;
import com.exequt.ecom.exception.UnknowPaymentFailureException;
import com.exequt.ecom.model.dto.PaymentRequest;
import com.exequt.ecom.model.entity.OrderStatus;
import com.exequt.ecom.model.dto.PaymentProviderCallback;
import com.exequt.ecom.model.dto.PaymentProviderRequest;
import com.exequt.ecom.model.entity.PaymentStatus;
import com.exequt.ecom.model.entity.OrderEntity;
import com.exequt.ecom.model.entity.PaymentEntity;
import com.exequt.ecom.repository.OrderRepository;
import com.exequt.ecom.repository.PaymentRepository;
import com.exequt.ecom.utils.Constants;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.http.HttpConnectTimeoutException;

@Service
@AllArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentProvider paymentProvider;
    public void recieveCallback(PaymentProviderCallback paymentProviderCallback) {
        PaymentEntity payment = paymentRepository.findByProviderAndProviderRef(
                paymentProviderCallback.getProvider(),
                paymentProviderCallback.getPaymentId()
        ).orElseThrow(() -> new PaymentNotFoundException(paymentProviderCallback.getProvider(), paymentProviderCallback.getPaymentId()));
        if (payment.getStatus() == PaymentStatus.COMPLETED || payment.getStatus() == PaymentStatus.FAILED) {
            // Idempotent handling: if payment is already completed or failed, do nothing
            return;
        }
        if(payment.getOrder().getStatus().equals(OrderStatus.CANCELLED)){
            // insert into refund table.
            return;
        }
        // Update payment status
        if (Constants.PAYMENT_PROVIDER_STATUS.equalsIgnoreCase(paymentProviderCallback.getStatus())) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(java.time.LocalDateTime.now());
            payment.getOrder().setStatus(OrderStatus.PAID);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.getOrder().setStatus(OrderStatus.PAYMENT_FAILED);
        }
        paymentRepository.save(payment);
        orderRepository.save(payment.getOrder());
    }

    public void startProcessPayment(Long orderId, PaymentRequest paymentRequest) {
        // 1. Validate order exists and is in correct state
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderStateException(order.getStatus().toString());
        }

        // 2. Create PaymentEntity
        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .provider("Fawary")
                .providerRef("fawary-ref-" + order.getId())
                .method("CARD")
                .status(PaymentStatus.PENDING)
                .amount(order.getTotal())
                .createdAt(java.time.LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);

        // 3. Call payment provider and handle exceptions
        try {
            paymentProvider.processPayment(
                    PaymentProviderRequest.builder()
                            .paymentAmount(payment.getAmount())
                            .paymentId(payment.getProviderRef())
                            .build()
            );
        }
        catch (HttpConnectTimeoutException e) {
            payment.setStatus(PaymentStatus.TIMEOUT); // it will be handled operationaly
            paymentRepository.save(payment);
            throw new UnknowPaymentFailureException();
        }
        catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            throw new PaymentFailureException(e.getMessage(), e);
        }
        // 4. Mark as pending until callback
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        orderRepository.save(order);
    }
}
