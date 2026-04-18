package com.exequt.ecom.controller;

import com.exequt.ecom.model.dto.*;
import com.exequt.ecom.service.CartService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartControllerTest {
    @Mock
    private CartService cartService;
    @InjectMocks
    private CartController cartController;

    private MockedStatic<ServletUriComponentsBuilder> servletUriBuilderMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servletUriBuilderMock = mockStatic(ServletUriComponentsBuilder.class);

        ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
        when(builder.path(anyString())).thenReturn(builder);

        // Mock the UriComponents and toUri chain
        UriComponents uriComponents = mock(UriComponents.class);
        when(builder.buildAndExpand()).thenReturn(uriComponents);
        when(uriComponents.toUri()).thenReturn(URI.create("/v1/carts/1"));

        // Mock both overloads of buildAndExpand to avoid ambiguity
        when(builder.buildAndExpand(any(Object[].class))).thenReturn(uriComponents);
        when(builder.buildAndExpand(anyMap())).thenReturn(uriComponents);

        servletUriBuilderMock.when(ServletUriComponentsBuilder::fromCurrentRequest)
                .thenReturn(builder);
    }

    @AfterEach
    void tearDown() {
        servletUriBuilderMock.close();
    }

    @Test
    void createEmptyCart_returnsCreated() {
        CartResponse cartResponse = CartResponse.builder().cartId(1L).build();
        when(cartService.createEmptyCart(1L)).thenReturn(cartResponse);
        ResponseEntity<CartResponse> response = cartController.createEmptyCart("corr", 1L);
        assertEquals(201, response.getStatusCode().value());
        assertEquals(cartResponse, response.getBody());
        assertNotNull(response.getHeaders().getLocation());
    }

    @Test
    void addItems_returnsOk() {
        CartDetailsResponse details = CartDetailsResponse.builder()
                .cartId(1L)
                .customerId(1L)
                .status("ACTIVE")
                .items(Collections.emptyList())
                .totalItems(0)
                .subtotal(BigDecimal.ZERO)
                .currency("USD")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CartRequest req = CartRequest.builder().items(Collections.emptyList()).build();
        when(cartService.addToActiveCart(1L, req)).thenReturn(details);
        ResponseEntity<CartDetailsResponse> response = cartController.addItems(1L, "corr", req);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(details, response.getBody());
    }

    @Test
    void addItemsToExistingCart_returnsOk() {
        CartDetailsResponse details = CartDetailsResponse.builder()
                .cartId(2L)
                .customerId(1L)
                .status("ACTIVE")
                .items(Collections.emptyList())
                .totalItems(0)
                .subtotal(BigDecimal.ZERO)

                .currency("USD")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CartRequest req = CartRequest.builder().items(Collections.emptyList()).build();
        when(cartService.addItemsToExistingCart(2L, 1L, req)).thenReturn(details);
        ResponseEntity<CartDetailsResponse> response = cartController.addItemsToExistingCart(1L, "corr", 2L, req);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(details, response.getBody());
    }

    @Test
    void viewCart_returnsOk() {
        CartDetailsResponse details = CartDetailsResponse.builder()
                .cartId(2L)
                .customerId(1L)
                .status("ACTIVE")
                .items(Collections.emptyList())
                .totalItems(0)
                .subtotal(BigDecimal.ZERO)
                .currency("USD")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        when(cartService.getCartDetails(2L)).thenReturn(details);
        ResponseEntity<CartDetailsResponse> response = cartController.viewCart(2L, "corr", 1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(details, response.getBody());
    }

    @Test
    void removeItems_returnsNoContent() {
        doNothing().when(cartService).removeItemFromCart(2L, 1L, 3);
        ResponseEntity<Void> response = cartController.removeItems(1L, "corr", 2L, 3);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void checkout_returnsOk() throws Exception {
        CheckoutResponse checkout = CheckoutResponse.builder().orderId(5L).build();
        CheckoutRequest checkoutRequest = CheckoutRequest.builder().build();
        when(cartService.checkoutCart(2L, 1L, checkoutRequest)).thenReturn(checkout);
        ResponseEntity<CheckoutResponse> response = cartController.checkout(1L, "corr", 2L, checkoutRequest);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(checkout, response.getBody());
    }
}
