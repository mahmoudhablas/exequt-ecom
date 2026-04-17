package com.exequt.ecom.service;

import com.exequt.ecom.model.dto.CartDetailsResponse;
import com.exequt.ecom.model.dto.CartItem;
import com.exequt.ecom.model.dto.CartRequest;
import com.exequt.ecom.model.entity.CartStatus;
import com.exequt.ecom.model.entity.CartEntity;
import com.exequt.ecom.model.entity.CartItemEntity;
import com.exequt.ecom.model.entity.CustomerEntity;
import com.exequt.ecom.model.entity.ProductEntity;
import com.exequt.ecom.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addToCart_createsNewCartAndAddsItem() {
        Long userId = 1L;
        CartRequest cartRequest = CartRequest.builder()
                .items(List.of(CartItem.builder().productId(2).quantity(3).build()))
                .build();
        CustomerEntity customer = new CustomerEntity();
        customer.setId(userId);
        ProductEntity product = new ProductEntity();
        product.setId(2);
        product.setPrice(BigDecimal.valueOf(10));
        CartEntity cart = CartEntity.builder().id(10L).customer(customer).status(CartStatus.ACTIVE).build();

        when(customerRepository.findById(userId)).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerIdAndStatus(userId, CartStatus.ACTIVE)).thenReturn(Optional.empty());
        when(cartRepository.save(any())).thenReturn(cart);
        when(productRepository.findById(2)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(10L, 2L)).thenReturn(Optional.empty());

        CartDetailsResponse response = cartService.addToActiveCart(userId, cartRequest);
        assertNotNull(response);
        assertEquals(10L, response.getCartId());
        verify(cartItemRepository).save(any(CartItemEntity.class));
    }
}

