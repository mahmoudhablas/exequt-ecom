package com.exequt.ecom.service;


import com.exequt.ecom.exception.*;
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


    private void validateCartOwnership(CartEntity cart, Long userId) {
        if (!cart.getCustomer().getId().equals(userId)) {
            throw new CartNotBelongCustomerException(cart.getId(), userId);
        }
        if (cart.getStatus() != CartStatus.ACTIVE) {
            throw new CartNotActiveException(cart.getId());
        }
    }

    private ProductEntity validateProductAvailable(Integer productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (product.getNumberInStock() <= 0) {
            throw new ProductOutOfStockException(productId);
        }
        return product;
    }

    private void addItemsToCart(CartEntity cart, CartRequest cartRequest) {
        for (CartItem reqItem : cartRequest.getItems()) {
            ProductEntity product = validateProductAvailable(reqItem.getProductId());
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

    @Transactional
    public CartResponse createEmptyCart(Long customerId) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        Optional<CartEntity> existingCart = cartRepository
                .findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE);
        if (existingCart.isPresent()) {
            return CartResponse.builder()
                    .cartId(existingCart.get().getId())
                    .build();
        }
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
            CartEntity cartEntity = cartRepository
                    .findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Failed to create or retrieve cart"));
            return CartResponse.builder()
                    .cartId(cartEntity.getId())
                    .build();
        }
    }

    @Transactional
    public CartDetailsResponse addToCart(Long userId, CartRequest cartRequest) {
        CustomerEntity customer = customerRepository.findById(userId)
                .orElseThrow(() -> new CustomerNotFoundException(userId));
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
        customerRepository.findById(userId)
                .orElseThrow(() -> new CustomerNotFoundException(userId));
        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
        validateCartOwnership(cart, userId);
        addItemsToCart(cart, cartRequest);
        return this.cartMapper.toCartDetailsResponse(cart);
    }

    public CartDetailsResponse getCartDetails(Long customerId) {
        CartEntity cart = cartRepository
                .findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NoActiveCartForCustomer(customerId));
        return this.cartMapper.toCartDetailsResponse(cart);
    }

    public void removeItemsfromExistingCart(Long cartId, Long userId, Integer productId) {
        customerRepository.findById(userId)
                .orElseThrow(() -> new CustomerNotFoundException(userId));
        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
        validateCartOwnership(cart, userId);
        CartItemEntity item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId.longValue())
                .orElseThrow(() -> new CartItemNotFoundException(cartId, productId));
        cartItemRepository.delete(item);

    }
}
