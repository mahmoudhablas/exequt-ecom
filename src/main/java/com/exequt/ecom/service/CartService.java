package com.exequt.ecom.service;


import com.exequt.ecom.mapper.CartMapper;
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
    private final CartMapper cartMapper;


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
    public CartDetailsResponse addToCart(Long userId, CartRequest cartRequest) {
        // Validate customer exists
        CustomerEntity customer = customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Find or create ACTIVE cart
        CartEntity cart = cartRepository.findByCustomerIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    CartEntity newCart = CartEntity.builder()
                            .customer(customer)
                            .status(CartStatus.ACTIVE)
                            .build();
                    return cartRepository.save(newCart);
                });

        addItemsToCart(cart, cartRequest);
        return this.cartMapper.toCartDetailsResponse(cart);
    }

    @Transactional
    public CartDetailsResponse addItemsToExistingCart(Long cartId, Long userId, CartRequest cartRequest) {
        // Validate customer exists
        CustomerEntity customer = customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Find the Cart by ID and ensure it belongs to the user and is ACTIVE
        CartEntity cart = cartRepository.findById(cartId).orElseThrow(
                () -> new RuntimeException("Cart not found")
        );
        if (!cart.getCustomer().getId().equals(userId)) {
            throw new RuntimeException("Cart does not belong to the user");
        }
        if (cart.getStatus() != CartStatus.ACTIVE) {
            throw new RuntimeException("Cart is not active");
        }

        addItemsToCart(cart, cartRequest);
        return this.cartMapper.toCartDetailsResponse(cart);
    }

    public CartDetailsResponse getCartDetails(Long customerId) {

        CartEntity cart = cartRepository
                .findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        return this.cartMapper.toCartDetailsResponse(cart);
    }

    private void addItemsToCart(CartEntity cart, CartRequest cartRequest) {
        for (CartItem reqItem : cartRequest.getItems()) {
            ProductEntity product = productRepository.findById(reqItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reqItem.getProductId()));
            if (product.getNumberInStock() <= 0) {
                throw new RuntimeException("Product out of stock: " + reqItem.getProductId());
            }
            CartItemEntity item = cartItemRepository
                    .findByCartIdAndProductId(cart.getId(), product.getId().longValue())
                    .orElse(null);

            if (item != null) {
                item.setQuantity(item.getQuantity() + reqItem.getQuantity());
            } else {
                item = CartItemEntity.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(reqItem.getQuantity())
                        .unitPrice(product.getPrice())
                        .build();
            }
            cartItemRepository.save(item);
        }
        cart.setUpdatedAt(java.time.LocalDateTime.now());
        cartRepository.save(cart);
    }
}
