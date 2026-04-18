package com.exequt.ecom.service;

import com.exequt.ecom.exception.CustomerNotFoundException;
import com.exequt.ecom.mapper.CartMapper;
import com.exequt.ecom.model.dto.CartRequest;
import com.exequt.ecom.model.dto.CartResponse;
import com.exequt.ecom.model.entity.CartEntity;
import com.exequt.ecom.model.entity.CartStatus;
import com.exequt.ecom.model.entity.CustomerEntity;
import com.exequt.ecom.repository.CartRepository;
import com.exequt.ecom.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CartMapper cartMapper;
    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEmptyCart_returnsCartResponse() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerIdAndStatus(1L, CartStatus.ACTIVE)).thenReturn(Optional.empty());
        CartEntity cart = CartEntity.builder().id(2L).customer(customer).status(CartStatus.ACTIVE).build();
        when(cartRepository.save(any())).thenReturn(cart);
        CartResponse response = cartService.createEmptyCart(1L);
        assertNotNull(response);
        assertEquals(2L, response.getCartId());
    }

    @Test
    void createEmptyCart_existingCart_returnsExisting() {
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        CartEntity cart = CartEntity.builder().id(2L).customer(customer).status(CartStatus.ACTIVE).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerIdAndStatus(1L, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        CartResponse response = cartService.createEmptyCart(1L);
        assertNotNull(response);
        assertEquals(2L, response.getCartId());
    }

    @Test
    void createEmptyCart_customerNotFound_throws() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> cartService.createEmptyCart(1L));
    }
}

