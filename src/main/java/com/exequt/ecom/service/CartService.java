package com.exequt.ecom.service;


import com.exequt.ecom.model.*;
import com.exequt.ecom.repository.CartItemRepository;
import com.exequt.ecom.repository.CartRepository;
import com.exequt.ecom.repository.CustomerRepository;
import com.exequt.ecom.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;


    @Transactional
     public CartResponse createEmptyCart(Long customerId) {

        // 1. Validate customer exists
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 2. Check if ACTIVE cart already exists
        Optional<CartEntity> existingCart = cartRepository
                .findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE);

        if (existingCart.isPresent()) {
            return CartResponse.builder()
                    .cartId(existingCart.get().getId())
                    .build(); // return existing instead of creating new
        }

        // 3. Try to create new cart
        try {
            CartEntity newCart = CartEntity.builder()
                    .customer(customer)
                    .status(CartStatus.ACTIVE)
                    .build();
            cartRepository.save(newCart);

            return CartResponse.builder()
                    .cartId(newCart.getId())
                    .build();

        } catch (DataIntegrityViolationException ex) {
            // 4. Handle race condition (unique constraint hit)
            // Another request created the cart at same time
            CartEntity cartEntity = cartRepository
                    .findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                    .orElseThrow(() ->
                            new RuntimeException("Failed to create or retrieve cart"));
            return CartResponse.builder()
                    .cartId(cartEntity.getId())
                    .build();

        }
    }

    @Transactional
    public CartResponse addToCart(Long userId, CartRequest cartRequest) {

        CartEntity cart = cartRepository
                .findByCustomerIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve cart"));

        cart = cartRepository.findWithLockById(cart.getId())
                .orElseThrow();

        for (CartItem reqItem : cartRequest.getItems()) {

            ProductEntity product = productRepository.findById(reqItem.getProductId())
                    .orElseThrow();

            CartItemEntity item = cartItemRepository
                    .findWithLockByCartIdAndProductId(cart.getId(), product.getId())
                    .orElse(null);

            if (item != null) {
                item.setQuantity(item.getQuantity() + reqItem.getQuantity());
            } else {
                item = CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(reqItem.getQuantity())
                        .unitPrice(product.getPrice())
                        .build();
            }

            cartItemRepository.save(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return CartMapper.toResponse(cart);
    }

