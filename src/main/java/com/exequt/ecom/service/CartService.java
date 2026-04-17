package com.exequt.ecom.service;

import com.exequt.ecom.exception.*;
import com.exequt.ecom.mapper.CartMapper;
import com.exequt.ecom.model.dto.*;
import com.exequt.ecom.model.entity.*;
import com.exequt.ecom.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.OptimisticLockException;

@Service
@AllArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;
    private final ProductService productService;


    private void addItemsToCart(CartEntity cart, CartRequest cartRequest) {
        log.info("Adding items to cart: cartId={}, items={}", cart.getId(), cartRequest.getItems().size());
        // 1. Extract all requested product IDs
        List<Integer> productIds = cartRequest.getItems().stream()
                .map(CartItem::getProductId)
                .toList();
        // 2. Single query — fetch all products at once
        List<ProductEntity> products = productService.getAvailableProducts(productIds);
        Map<Integer, ProductEntity> productMap = products.stream()
                .collect(Collectors.toMap(ProductEntity::getId, p -> p));
        // 3. Single query — fetch existing cart items for this cart
        Map<Integer, CartItemEntity> existingItems = cartItemRepository
                .findByCartId(cart.getId())
                .stream()
                .collect(Collectors.toMap(
                        ci -> ci.getProduct().getId(), ci -> ci
                ));
        // 4. Merge in memory — no per-item DB calls
        List<CartItemEntity> toSave = new ArrayList<>();
        for (CartItem reqItem : cartRequest.getItems()) {
            ProductEntity product = productMap.get(reqItem.getProductId());
            if (product == null) throw new ProductNotFoundException(reqItem.getProductId());

            CartItemEntity item = existingItems.getOrDefault(
                    product.getId(),
                    CartItemEntity.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(0)
                            .unitPrice(product.getPrice())
                            .build()
            );
            item.setQuantity(item.getQuantity() + reqItem.getQuantity());
            toSave.add(item);
        }
        // 5. Single bulk save
        cartItemRepository.saveAll(toSave);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        log.info("Items added to cart: cartId={}", cart.getId());
    }

    @Transactional
    public CartResponse createEmptyCart(Long customerId) {
        log.info("Creating empty cart for customerId={}", customerId);
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        Optional<CartEntity> existingCart = cartRepository
                .findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE);
        if (existingCart.isPresent()) {
            log.info("Active cart already exists for customerId={}, cartId={}", customerId, existingCart.get().getId());
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
            log.info("New cart created: cartId={}, customerId={}", newCart.getId(), customerId);
            return CartResponse.builder()
                    .cartId(newCart.getId())
                    .build();
        } catch (DataIntegrityViolationException ex) {
            log.error("Race condition on cart creation for customerId={}", customerId, ex);
            CartEntity cartEntity = cartRepository
                    .findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                    .orElseThrow(() -> new RuntimeException("Failed to create or retrieve cart"));
            return CartResponse.builder()
                    .cartId(cartEntity.getId())
                    .build();
        }
    }
    @Transactional
    public CartDetailsResponse addToActiveCart(Long userId, CartRequest cartRequest) {
        log.info("Adding items to active cart for userId={}, items={}", userId, cartRequest.getItems().size());
        CustomerEntity customer = customerRepository.findById(userId)
                .orElseThrow(() -> new CustomerNotFoundException(userId));
        CartEntity cart = cartRepository.findByCustomerIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    CartEntity newCart = CartEntity.builder()
                            .customer(customer)
                            .status(CartStatus.ACTIVE)
                            .build();
                    log.info("No active cart found, creating new cart for userId={}", userId);
                    return cartRepository.save(newCart);
                });
        addItemsToCart(cart, cartRequest);
        log.info("Items added to active cart for userId={}, cartId={}", userId, cart.getId());
        return this.cartMapper.toCartDetailsResponse(cart);
    }
    @Transactional
    public CartDetailsResponse addItemsToExistingCart(Long cartId, Long userId, CartRequest cartRequest) {
        log.info("Adding items to existing cart: cartId={}, userId={}, items={}", cartId, userId, cartRequest.getItems().size());
        try {
            customerRepository.findById(userId)
                    .orElseThrow(() -> new CustomerNotFoundException(userId));
            CartEntity cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new CartNotFoundException(cartId));
            cart.assertOwnership(userId);
            addItemsToCart(cart, cartRequest);
            log.info("Items added to cartId={}", cartId);
            return this.cartMapper.toCartDetailsResponse(cart);
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock failure for cartId={}", cartId);
            throw new CartConcurrencyException(cartId);
        }
    }
    public CartDetailsResponse getCartDetails(Long cartId) {
        log.info("Fetching cart details: cartId={}", cartId);
        CartEntity cart = cartRepository
                .findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
        return this.cartMapper.toCartDetailsResponse(cart);
    }
    @Transactional
    public void removeItemFromCart(Long cartId, Long userId, Integer productId) {
        log.info("Removing item from cart: cartId={}, userId={}, productId={}", cartId, userId, productId);
        try {
            CartEntity cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new CartNotFoundException(cartId));
            cart.assertOwnership(userId);
            CartItemEntity item = cartItemRepository
                    .findByCartIdAndProductId(cart.getId(), productId.longValue())
                    .orElseThrow(() -> new CartItemNotFoundException(cartId, productId));
            cartItemRepository.delete(item);
            log.info("Item removed from cart: cartId={}, productId={}", cartId, productId);
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock failure for cartId={}", cartId);
            throw new CartConcurrencyException(cartId);
        }
    }
    @Transactional
    public CheckoutResponse checkoutCart(Long cartId, Long userId) throws Exception {
        log.info("Checkout started: cartId={}, userId={}", cartId, userId);
        try {
            CartEntity cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new CartNotFoundException(cartId));
            cart.assertOwnership(userId);
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new CartEmptyException(cartId);
            }
            if (cart.getStatus() != CartStatus.ACTIVE) {
                throw new CartNotActiveException(cartId);
            }
            cart.checkout();
            cartRepository.save(cart);
            OrderEntity order = orderFactory.createFromCart(cart);
            order = orderRepository.save(order);
            log.info("Order created: orderId={}, cartId={}", order.getId(), cartId);
            return CheckoutResponse.builder()
                    .orderId(order.getId())
                    .build();
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock failure for cartId={}", cartId);
            throw new CartConcurrencyException(cartId);
        }
    }
}
