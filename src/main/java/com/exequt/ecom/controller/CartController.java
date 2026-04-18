package com.exequt.ecom.controller;

import com.exequt.ecom.model.dto.*;
import com.exequt.ecom.service.CartService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/carts")
@AllArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> createEmptyCart(
            @RequestHeader ("X-Correlation-Id") String correlationId,
            @RequestHeader ("X-User-Id") Long userId) {
        log.info("[createEmptyCart] userId={}, correlationId={}", userId, correlationId);
        CartResponse cartResponse = this.cartService.createEmptyCart(userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cartId}")
                .buildAndExpand(cartResponse.getCartId())
                .toUri();
        log.info("[createEmptyCart] Cart created: cartId={}", cartResponse.getCartId());
        return ResponseEntity.created(location).body(cartResponse);
    }
    @PostMapping("/active/items")
    public ResponseEntity<CartDetailsResponse> addItems(
            @RequestHeader ("X-User-Id") Long userId,
            @RequestHeader ("X-Correlation-Id") String correlationId,
            @RequestBody CartRequest cartRequest){
        log.info("[addItems] userId={}, correlationId={}, items={}", userId, correlationId, cartRequest.getItems().size());
        CartDetailsResponse cartResponse =  this.cartService.addToActiveCart(userId, cartRequest);
        log.info("[addItems] Items added to active cart for userId={}", userId);
        return ResponseEntity.ok(cartResponse);
    }
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartDetailsResponse> addItemsToExistingCart(
            @RequestHeader ("X-User-Id") Long userId,
            @RequestHeader ("X-Correlation-Id") String correlationId,
            @PathVariable Long cartId,
            @RequestBody CartRequest cartRequest){
        log.info("[addItemsToExistingCart] userId={}, cartId={}, correlationId={}, items={}", userId, cartId, correlationId, cartRequest.getItems().size());
        CartDetailsResponse cartResponse =  this.cartService.addItemsToExistingCart(cartId, userId, cartRequest);
        log.info("[addItemsToExistingCart] Items added to cartId={}", cartId);
        return ResponseEntity.ok(cartResponse);
    }
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDetailsResponse> viewCart(@PathVariable Long cartId,
                                                        @RequestHeader ("X-Correlation-Id") String correlationId,
                                                        @RequestHeader ("X-User-Id") Long userId){
        log.info("[viewCart] userId={}, cartId={}, correlationId={}", userId, cartId, correlationId);
        CartDetailsResponse response = cartService.getCartDetails(cartId);
        log.info("[viewCart] Cart details returned for cartId={}", cartId);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> removeItems(
            @RequestHeader ("X-User-Id") Long userId,
            @RequestHeader ("X-Correlation-Id") String correlationId,
            @PathVariable Long cartId,
            @PathVariable Integer productId){
        log.info("[removeItems] userId={}, cartId={}, productId={}, correlationId={}", userId, cartId, productId, correlationId);
        this.cartService.removeItemFromCart(cartId, userId, productId);
        log.info("[removeItems] Item removed from cartId={}, productId={}", cartId, productId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{cartId}/checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            @RequestHeader ("X-User-Id") Long userId,
            @RequestHeader ("X-Correlation-Id") String correlationId,
            @PathVariable Long cartId,
            @RequestBody CheckoutRequest checkoutRequest) throws Exception {
        log.info("[checkout] userId={}, cartId={}, correlationId={}", userId, cartId, correlationId);
        CheckoutResponse response = this.cartService.checkoutCart(cartId, userId, checkoutRequest);
        log.info("[checkout] Checkout completed for cartId={}, orderId={}", cartId, response.getOrderId());
        return ResponseEntity.ok(response);
    }

}
