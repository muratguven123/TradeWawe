package com.murat.tradewave.Service;

import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.dto.Cart.RemoveCartRequest;
import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.CartItem;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.model.OrderItem;
import com.murat.tradewave.repository.CartItemRepository;
import com.murat.tradewave.repository.CartRepository;
import com.murat.tradewave.service.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void addToCart_createsCartAndAddsItemWhenMissing() {
        AddToCartRequest request = AddToCartRequest.builder()
                .userid(1L)
                .productid(10L)
                .quantity(2)
                .build();

        when(cartRepository.findCartByid(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.addToCart(request);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository, times(2)).save(captor.capture());
        Cart savedCart = captor.getAllValues().get(1);
        assertThat(savedCart.getUserId()).isEqualTo(1L);
        assertThat(savedCart.getItems()).hasSize(1);
        CartItem item = savedCart.getItems().get(0);
        assertThat(item.getProductId()).isEqualTo(10L);
        assertThat(item.getQuantity()).isEqualTo(2);
    }

    @Test
    void addToCart_incrementsQuantityForExistingItem() {
        CartItem existingItem = CartItem.builder()
                .productId(10L)
                .quantity(1)
                .build();
        Cart cart = Cart.builder()
                .id(1L)
                .userId(1L)
                .items(new ArrayList<>(List.of(existingItem)))
                .checkedout(false)
                .build();
        existingItem.setCart(cart);

        when(cartRepository.findCartByid(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        AddToCartRequest request = AddToCartRequest.builder()
                .userid(1L)
                .productid(10L)
                .quantity(2)
                .build();

        cartService.addToCart(request);

        assertThat(existingItem.getQuantity()).isEqualTo(3);
        verify(cartRepository).save(cart);
    }

    @Test
    void removeFromCart_deletesItemAndSavesCart() {
        CartItem item = CartItem.builder()
                .productId(10L)
                .quantity(1)
                .build();
        Cart cart = Cart.builder()
                .id(1L)
                .userId(1L)
                .items(new ArrayList<>(List.of(item)))
                .checkedout(false)
                .build();
        item.setCart(cart);

        when(cartRepository.findCartByid(1L)).thenReturn(Optional.of(cart));

        RemoveCartRequest request = new RemoveCartRequest();
        request.setUserid(1L);
        request.setProductid(10L);

        cartService.removeFromCart(request);

        assertThat(cart.getItems()).isEmpty();
        verify(cartItemRepository).delete(item);
        verify(cartRepository).save(cart);
    }

    @Test
    void viewCart_returnsCartFromRepository() {
        Cart cart = new Cart();
        when(cartRepository.findCartByid(1L)).thenReturn(Optional.of(cart));

        Cart result = cartService.viewCart(1L);

        assertThat(result).isEqualTo(cart);
        verify(cartRepository).findCartByid(1L);
    }

    @Test
    void checkoutCart_buildsOrderAndMarksCartCheckedOut() {
        CartItem item = CartItem.builder()
                .productId(5L)
                .quantity(2)
                .build();
        Cart cart = Cart.builder()
                .id(1L)
                .userId(1L)
                .items(new ArrayList<>(List.of(item)))
                .checkedout(false)
                .build();
        item.setCart(cart);

        when(cartRepository.findCartByid(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        Order order = cartService.checkoutCart(1L);

        assertThat(order.getId()).isEqualTo(1L);
        assertThat(order.getItems()).hasSize(1);
        OrderItem orderItem = order.getItems().get(0);
        assertThat(orderItem.getId()).isEqualTo(5L);
        assertThat(orderItem.getQuantity()).isEqualTo(2);
        assertThat(orderItem.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(5));
        assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(10));
        assertThat(orderItem.getOrder()).isEqualTo(order);
        assertThat(cart.isCheckedout()).isTrue();
        verify(cartRepository).save(cart);
    }
}