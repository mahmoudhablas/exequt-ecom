package com.exequt.ecom.controller;

import com.exequt.ecom.model.CartDetailsResponse;
import com.exequt.ecom.model.CartRequest;
import com.exequt.ecom.model.CartResponse;
import com.exequt.ecom.model.ProductResponse;
import com.exequt.ecom.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/carts")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> createEmptyCart(@RequestHeader ("X-User-Id") Long userId)
    {
       CartResponse cartResponse =  this.cartService.createEmptyCart(userId);
       return ResponseEntity.ok(cartResponse);
    }
    @PostMapping("/items")
    public ResponseEntity<CartDetailsResponse> addItems(
            @RequestHeader ("X-User-Id") Long userId,
             @RequestBody CartRequest cartRequest){
        CartDetailsResponse cartResponse =  this.cartService.addToCart(userId, cartRequest);
        return ResponseEntity.ok(cartResponse);
    }
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartDetailsResponse> addItemsToExistingCart(
            @RequestHeader ("X-User-Id") Long userId,
            @PathVariable Long cartId,
            @RequestBody CartRequest cartRequest){
        CartDetailsResponse cartResponse =  this.cartService.addItemsToExistingCart(cartId, userId, cartRequest);
        return ResponseEntity.ok(cartResponse);
    }
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDetailsResponse> viewCart(@PathVariable Long cartId,
                                                        @RequestHeader ("X-User-Id") Long userId){
        return ResponseEntity.ok(cartService.getCartDetails(userId));
    }

}
