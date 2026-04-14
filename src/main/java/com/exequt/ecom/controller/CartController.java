package com.exequt.ecom.controller;

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
    public ResponseEntity<CartResponse> createCart(@RequestHeader ("X-User-Id") Long userId)
   {
       CartResponse cartResponse =  this.cartService.createEmptyCart(userId);
       return ResponseEntity.ok(cartResponse);
    }
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponse> addItems(
            @RequestHeader ("X-User-Id") Long userId,
            @PathVariable String cartId, @RequestBody CartRequest cartRequest){
        CartResponse cartResponse =  this.cartService.addToCart(userId, cartRequest);
        return ResponseEntity.ok(cartResponse);
    }


}
