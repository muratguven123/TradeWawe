package com.murat.tradewave.controller;

import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.dto.Cart.RemoveCartRequest;
import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.service.CartService;
import com.murat.tradewave.service.CartServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartServiceImpl cartServiceImpl;
    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(@RequestBody AddToCartRequest request) {
        cartServiceImpl.addToCart(request);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromCart(@RequestParam RemoveCartRequest removeCartRequest) {
        cartServiceImpl.removeFromCart(removeCartRequest);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/view")
    public ResponseEntity<Cart> viewCart(@RequestParam Long userId) {
        Cart cart = cartServiceImpl.viewCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkoutCart(@RequestParam Long orderid) {
        Order order = cartServiceImpl.checkoutCart(orderid);
        return ResponseEntity.ok(order);
    }

















}
