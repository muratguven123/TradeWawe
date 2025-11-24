package com.murat.tradewave.Controller;

import com.murat.tradewave.controller.CartController;
import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.dto.Cart.RemoveCartRequest;
import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.service.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartServiceImpl cartService;

    private CartController cartController;

    @BeforeEach
    void setUp() {
        cartController = new CartController(cartService);
    }

    @Test
    void addToCartDelegatesToService() {
        AddToCartRequest request = new AddToCartRequest();

        ResponseEntity<Void> response = cartController.addToCart(request);

        verify(cartService).addToCart(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void removeFromCartDelegatesToService() {
        RemoveCartRequest request = new RemoveCartRequest();

        ResponseEntity<Void> response = cartController.removeFromCart(request);

        verify(cartService).removeFromCart(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void viewCartReturnsCartFromService() {
        Cart cart = new Cart();
        when(cartService.viewCart(1L)).thenReturn(cart);

        ResponseEntity<Cart> response = cartController.viewCart(1L);

        assertEquals(cart, response.getBody());
    }

    @Test
    void checkoutCartReturnsOrderFromService() {
        Order order = new Order();
        when(cartService.checkoutCart(1L)).thenReturn(order);

        ResponseEntity<Order> response = cartController.checkoutCart(1L);

        assertEquals(order, response.getBody());
    }
}