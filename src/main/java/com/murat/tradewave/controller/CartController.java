package com.murat.tradewave.controller;

import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor

public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(@RequestBody AddToCartRequest request) {
        cartService.addToCart(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromCart(@RequestParam Long userId, @RequestParam Long productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Cart> viewCart(@RequestParam Long userId) {
        Cart cart = cartService.viewCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkoutCart(@RequestParam Long userId) {
        Order order = cartService.checkoutCart(userId);
        return ResponseEntity.ok(order);
    }

















}
