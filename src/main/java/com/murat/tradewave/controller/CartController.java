package com.murat.tradewave.controller;

import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.dto.Cart.RemoveCartRequest;
import com.murat.tradewave.dto.Cartİtem.AddToCartItem;
import com.murat.tradewave.dto.Cartİtem.DeleteCartItem;
import com.murat.tradewave.dto.Cartİtem.ViewCartItems;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.service.CartItemService;
import com.murat.tradewave.service.CartServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartServiceImpl cartService;
    private final Mapper mapper;
    private final CartItemService cartItemService;

    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(@Valid @RequestBody AddToCartRequest request) {
        log.info("Received add to cart request - UserId: {}, ProductId: {}, Quantity: {}",
                 request.getUserId(), request.getProductId(), request.getQuantity());

        cartService.addToCart(request);
        log.info("Successfully added product {} to cart for user {}",
                 request.getProductId(), request.getUserId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromCart(@RequestBody RemoveCartRequest removeCartRequest) {
        cartService.removeFromCart(removeCartRequest);
        return ResponseEntity.ok().build();
    }

   @GetMapping("/viewCart")
   public ResponseEntity<ViewCartItems> getCartItems(@RequestParam Long userId){
         ViewCartItems viewCartItems = cartService.viewCartByUserId(userId);
        return ResponseEntity.ok(viewCartItems);
   }

    @PostMapping("/checkout")
    public ResponseEntity<com.murat.tradewave.dto.Order.OrderResponseDto> checkoutCart(@RequestParam Long userId) {
        com.murat.tradewave.dto.Order.OrderResponseDto order = cartService.checkoutCart(userId);
        return ResponseEntity.ok(order);
    }
@DeleteMapping("/deleteCartItem")
    public ResponseEntity<Void> deleteCart(@RequestBody DeleteCartItem deleteCartItem){
        cartItemService.saveDeleteCartItem(deleteCartItem);
        return ResponseEntity.ok().build();
}
@PostMapping("/addCartItem")
    public ResponseEntity<Void> addCartItem(@RequestBody AddToCartItem addToCartItem){
        cartItemService.saveCartItem(addToCartItem);
        return ResponseEntity.ok().build();
}

}
