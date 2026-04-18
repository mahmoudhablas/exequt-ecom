package com.exequt.ecom.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.exequt.ecom.exception.PaymentFailureException;
import com.exequt.ecom.model.dto.PaymentProviderRequest;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
@Service
public class PaymentProvider {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void processPayment(PaymentProviderRequest paymentProviderRequest) throws HttpConnectTimeoutException {
        try {
            String payload = objectMapper.writeValueAsString(paymentProviderRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/internal/v1/payments"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<Void> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new PaymentFailureException("Payment provider returned status: " + response.statusCode());
            }
        }   catch (HttpConnectTimeoutException e) {
            throw e;
        }
        catch (PaymentFailureException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentFailureException("Payment processing failed: " + e.getMessage(), e);
        }
    }
}
