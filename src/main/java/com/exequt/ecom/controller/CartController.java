package com.exequt.ecom.controller;

import com.exequt.ecom.model.CartDetailsResponse;
import com.exequt.ecom.model.CartRequest;
import com.exequt.ecom.model.CartResponse;
import com.exequt.ecom.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/carts")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> createEmptyCart(@RequestHeader ("X-User-Id") Long userId) {
        CartResponse cartResponse = this.cartService.createEmptyCart(userId);
        // Build Location URI for the created cart
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{cartId}")
                .buildAndExpand(cartResponse.getCartId())
                .toUri();
        return ResponseEntity.created(location).body(cartResponse);
    }
    @PostMapping("/active/items")
    public ResponseEntity<CartDetailsResponse> addItems(
            @RequestHeader ("X-User-Id") Long userId,
            @RequestHeader ("X-Correlation-Id") String correlationId,
            @RequestBody CartRequest cartRequest){
        CartDetailsResponse cartResponse =  this.cartService.addToCart(userId, cartRequest);
        return ResponseEntity.ok(cartResponse);
    }
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartDetailsResponse> addItemsToExistingCart(
            @RequestHeader ("X-User-Id") Long userId,
            @RequestHeader ("X-Correlation-Id") String correlationId,
            @PathVariable Long cartId,
            @RequestBody CartRequest cartRequest){
        CartDetailsResponse cartResponse =  this.cartService.addItemsToExistingCart(cartId, userId, cartRequest);
        return ResponseEntity.ok(cartResponse);
    }
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDetailsResponse> viewCart(@PathVariable Long cartId,
                                                        @RequestHeader ("X-Correlation-Id") String correlationId,
                                                        @RequestHeader ("X-User-Id") Long userId){
        return ResponseEntity.ok(cartService.getCartDetails(userId));
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<Void> removeItems(
            @RequestHeader ("X-User-Id") Long userId,
            @RequestHeader ("X-Correlation-Id") String correlationId,
            @PathVariable Long cartId,
            @PathVariable Integer productId){
         this.cartService.removeItemsfromExistingCart(cartId, userId, productId);
        return ResponseEntity.noContent().build();
    }

}
