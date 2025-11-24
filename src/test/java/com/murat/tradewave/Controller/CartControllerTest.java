package com.murat.tradewave.Controller;

import com.murat.tradewave.controller.CartController;
import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.dto.Cart.RemoveCartRequest;
import com.murat.tradewave.dto.Cartİtem.AddToCartItem;
import com.murat.tradewave.dto.Cartİtem.DeleteCartItem;
import com.murat.tradewave.dto.Cartİtem.ViewCartItems;
import com.murat.tradewave.dto.Order.OrderResponseDto;
import com.murat.tradewave.exception.CartException;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.service.CartItemService;
import com.murat.tradewave.service.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartServiceImpl cartService;

    @Mock
    private Mapper mapper;

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartController cartController;

    private AddToCartRequest addToCartRequest;
    private RemoveCartRequest removeCartRequest;
    private ViewCartItems viewCartItems;
    private OrderResponseDto orderResponseDto;
    private AddToCartItem addToCartItem;
    private DeleteCartItem deleteCartItem;

    @BeforeEach
    void setUp() {
        // Prepare test data
        addToCartRequest = new AddToCartRequest();
        addToCartRequest.setUserId(1L);
        addToCartRequest.setProductId(100L);
        addToCartRequest.setQuantity(2);

        removeCartRequest = new RemoveCartRequest();
        removeCartRequest.setUserId(1L);
        removeCartRequest.setProductId(100L);

        viewCartItems = new ViewCartItems();

        orderResponseDto = OrderResponseDto.builder()
                .orderId(10L)
                .userId(1L)
                .build();

        addToCartItem = new AddToCartItem();
        deleteCartItem = new DeleteCartItem();
    }

    @Test
    void addToCart_ShouldAddItemToCart_AndReturnOk() {
        // When
        ResponseEntity<Void> response = cartController.addToCart(addToCartRequest);

        // Then
        verify(cartService, times(1)).addToCart(addToCartRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addToCart_ShouldThrowException_WhenInvalidRequest() {
        // Given
        doThrow(new IllegalArgumentException("Invalid product ID"))
                .when(cartService).addToCart(any(AddToCartRequest.class));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cartController.addToCart(addToCartRequest));
        verify(cartService, times(1)).addToCart(addToCartRequest);
    }

    @Test
    void removeFromCart_ShouldRemoveItemFromCart_AndReturnOk() {
        // When
        ResponseEntity<Void> response = cartController.removeFromCart(removeCartRequest);

        // Then
        verify(cartService, times(1)).removeFromCart(removeCartRequest);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void removeFromCart_ShouldThrowException_WhenItemNotFound() {
        // Given
        doThrow(new CartException("Item not found in cart"))
                .when(cartService).removeFromCart(any(RemoveCartRequest.class));

        // When & Then
        assertThrows(CartException.class, () -> cartController.removeFromCart(removeCartRequest));
        verify(cartService, times(1)).removeFromCart(removeCartRequest);
    }

    @Test
    void getCartItems_ShouldReturnViewCartItems_WhenUserIdIsValid() {
        // Given
        Long userId = 1L;
        when(cartService.viewCartByUserId(userId)).thenReturn(viewCartItems);

        // When
        ResponseEntity<ViewCartItems> response = cartController.getCartItems(userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(viewCartItems, response.getBody());
        verify(cartService, times(1)).viewCartByUserId(userId);
    }

    @Test
    void getCartItems_ShouldThrowException_WhenUserIdNotFound() {
        // Given
        Long userId = 99L;
        when(cartService.viewCartByUserId(userId))
                .thenThrow(new CartException("Cart not found for user: " + userId));

        // When & Then
        assertThrows(CartException.class, () -> cartController.getCartItems(userId));
        verify(cartService, times(1)).viewCartByUserId(userId);
    }

    @Test
    void checkoutCart_ShouldReturnOrderResponseDto_WhenCheckoutSuccessful() {
        // Given
        Long userId = 1L;
        when(cartService.checkoutCart(userId)).thenReturn(orderResponseDto);

        // When
        ResponseEntity<OrderResponseDto> response = cartController.checkoutCart(userId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderResponseDto, response.getBody());
        assertEquals(10L, response.getBody().getOrderId());
        verify(cartService, times(1)).checkoutCart(userId);
    }

    @Test
    void checkoutCart_ShouldThrowException_WhenCartIsEmpty() {
        // Given
        Long userId = 2L;
        when(cartService.checkoutCart(userId))
                .thenThrow(new RuntimeException("Cart is empty"));

        // When & Then
        assertThrows(RuntimeException.class, () -> cartController.checkoutCart(userId));
        verify(cartService, times(1)).checkoutCart(userId);
    }

    @Test
    void deleteCartItem_ShouldDeleteItem_AndReturnOk() {
        // When
        ResponseEntity<Void> response = cartController.deleteCart(deleteCartItem);

        // Then
        verify(cartItemService, times(1)).saveDeleteCartItem(deleteCartItem);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteCartItem_ShouldThrowException_WhenItemNotFound() {
        // Given
        doThrow(new CartException("Cart item not found"))
                .when(cartItemService).saveDeleteCartItem(any(DeleteCartItem.class));

        // When & Then
        assertThrows(CartException.class, () -> cartController.deleteCart(deleteCartItem));
        verify(cartItemService, times(1)).saveDeleteCartItem(deleteCartItem);
    }

    @Test
    void addCartItem_ShouldAddItem_AndReturnOk() {
        // When
        ResponseEntity<Void> response = cartController.addCartItem(addToCartItem);

        // Then
        verify(cartItemService, times(1)).saveCartItem(addToCartItem);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void addCartItem_ShouldThrowException_WhenValidationFails() {
        // Given
        doThrow(new IllegalArgumentException("Invalid cart item data"))
                .when(cartItemService).saveCartItem(any(AddToCartItem.class));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cartController.addCartItem(addToCartItem));
        verify(cartItemService, times(1)).saveCartItem(addToCartItem);
    }
}