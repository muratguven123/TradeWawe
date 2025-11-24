package com.murat.tradewave.Service;

import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.dto.Cart.RemoveCartRequest;
import com.murat.tradewave.dto.Order.OrderResponseDto;
import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.CartItem;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.model.OrderItem;
import com.murat.tradewave.model.Product;
import com.murat.tradewave.model.User;
import com.murat.tradewave.repository.CartItemRepository;
import com.murat.tradewave.repository.CartRepository;
import com.murat.tradewave.repository.ProductionRepository;
import com.murat.tradewave.repository.UserRepository;
import com.murat.tradewave.repository.OrderRepository;
import com.murat.tradewave.service.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductionRepository productionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private Product testProduct;
    private Cart testCart;
    private User testUser;
    private AddToCartRequest addToCartRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .password("password123")
                .build();

        testProduct = Product.builder()
                .id(10L)
                .name("Test Product")
                .stock(100)
                .price(BigDecimal.valueOf(50))
                .build();

        testCart = Cart.builder()
                .id(1L)
                .user(testUser)
                .items(new ArrayList<>())
                .checkedout(false)
                .build();
    }

    @Test
    void addToCart_shouldCreateNewCartAndAddItem_whenCartDoesNotExist() {
        // Given
        addToCartRequest = AddToCartRequest.builder()
                .userId(1L)
                .productId(10L)
                .quantity(2)
                .build();

        when(productionRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.emptyList());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        cartService.addToCart(addToCartRequest);

        // Then
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository, times(2)).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getAllValues().get(1);
        assertThat(savedCart.getUser().getId()).isEqualTo(1L);
        assertThat(savedCart.getItems()).hasSize(1);
        assertThat(savedCart.getItems().get(0).getProduct().getId()).isEqualTo(10L);
        assertThat(savedCart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(savedCart.isCheckedout()).isFalse();
    }

    @Test
    void addToCart_shouldIncrementQuantity_whenProductAlreadyInCart() {
        // Given
        CartItem existingItem = CartItem.builder()
                .product(testProduct)
                .quantity(3)
                .cart(testCart)
                .build();

        testCart.getItems().add(existingItem);

        addToCartRequest = AddToCartRequest.builder()
                .userId(1L)
                .productId(10L)
                .quantity(2)
                .build();

        when(productionRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.singletonList(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // When
        cartService.addToCart(addToCartRequest);

        // Then
        assertThat(existingItem.getQuantity()).isEqualTo(5);
        verify(cartRepository).save(testCart);
    }

    @Test
    void addToCart_shouldThrowException_whenProductNotFound() {
        // Given
        addToCartRequest = AddToCartRequest.builder()
                .userId(1L)
                .productId(10L)
                .quantity(2)
                .build();

        when(productionRepository.findById(10L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cartService.addToCart(addToCartRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void addToCart_shouldThrowException_whenInsufficientStock() {
        // Given
        testProduct.setStock(1);

        addToCartRequest = AddToCartRequest.builder()
                .userId(1L)
                .productId(10L)
                .quantity(5)
                .build();

        when(productionRepository.findById(10L)).thenReturn(Optional.of(testProduct));

        // When & Then
        assertThatThrownBy(() -> cartService.addToCart(addToCartRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void addToCart_shouldThrowException_whenIncrementingQuantityExceedsStock() {
        // Given
        testProduct.setStock(5);

        CartItem existingItem = CartItem.builder()
                .product(testProduct)
                .quantity(4)
                .cart(testCart)
                .build();

        testCart.getItems().add(existingItem);

        addToCartRequest = AddToCartRequest.builder()
                .userId(1L)
                .productId(10L)
                .quantity(2)
                .build();

        when(productionRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.singletonList(testCart));

        // When & Then
        assertThatThrownBy(() -> cartService.addToCart(addToCartRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void removeFromCart_shouldRemoveItemAndSaveCart() {
        // Given
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(testProduct)
                .quantity(2)
                .cart(testCart)
                .build();

        testCart.getItems().add(cartItem);

        RemoveCartRequest removeRequest = new RemoveCartRequest();
        removeRequest.setUserId(1L);
        removeRequest.setProductId(10L);

        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.singletonList(testCart));

        // When
        cartService.removeFromCart(removeRequest);

        // Then
        assertThat(testCart.getItems()).isEmpty();
        verify(cartItemRepository).delete(cartItem);
        verify(cartRepository).save(testCart);
    }

    @Test
    void removeFromCart_shouldThrowException_whenCartNotFound() {
        // Given
        RemoveCartRequest removeRequest = new RemoveCartRequest();
        removeRequest.setUserId(1L);
        removeRequest.setProductId(10L);

        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> cartService.removeFromCart(removeRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cart not found for user");
    }

    @Test
    void removeFromCart_shouldThrowException_whenProductNotInCart() {
        // Given
        RemoveCartRequest removeRequest = new RemoveCartRequest();
        removeRequest.setUserId(1L);
        removeRequest.setProductId(999L);

        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.singletonList(testCart));

        // When & Then
        assertThatThrownBy(() -> cartService.removeFromCart(removeRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found in cart");
    }

    @Test
    void viewCart_shouldReturnCart_whenCartExists() {
        // Given
        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.singletonList(testCart));

        // When
        Cart result = cartService.viewCart(1L);

        // Then
        assertThat(result).isEqualTo(testCart);
        assertThat(result.getUser().getId()).isEqualTo(1L);
        verify(cartRepository).findActiveCartsByUserId(1L);
    }

    @Test
    void viewCart_shouldThrowException_whenCartNotFound() {
        // Given
        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> cartService.viewCart(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cart not found");
    }

    @Test
    void checkoutCart_shouldCreateOrderAndMarkCartAsCheckedOut() {
        // Given
        CartItem cartItem = CartItem.builder()
                .product(testProduct)
                .quantity(2)
                .cart(testCart)
                .build();

        testCart.getItems().add(cartItem);

        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.singletonList(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productionRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // When
        OrderResponseDto orderResponse = cartService.checkoutCart(1L);

        // Then
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getItems()).hasSize(1);
        assertThat(orderResponse.getUserId()).isEqualTo(testUser.getId());

        var orderItemDto = orderResponse.getItems().get(0);
        assertThat(orderItemDto.getProductId()).isEqualTo(10L);
        assertThat(orderItemDto.getQuantity()).isEqualTo(2);
        assertThat(orderItemDto.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(orderItemDto.getSubtotal()).isEqualByComparingTo(BigDecimal.valueOf(100));

        assertThat(orderResponse.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(orderResponse.getCreatedAt()).isNotNull();

        assertThat(testCart.isCheckedout()).isTrue();
        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).save(testCart);
        verify(productionRepository).save(testProduct);
    }

    @Test
    void checkoutCart_shouldThrowException_whenCartNotFound() {
        // Given
        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> cartService.checkoutCart(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cart not found for user");
    }

    @Test
    void checkoutCart_shouldThrowException_whenCartIsEmpty() {
        // Given
        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.singletonList(testCart));

        // When & Then
        assertThatThrownBy(() -> cartService.checkoutCart(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cart is empty");
    }

    @Test
    void checkoutCart_shouldThrowException_whenProductNotFound() {
        // Given
        CartItem cartItem = CartItem.builder()
                .product(testProduct)
                .quantity(2)
                .cart(testCart)
                .build();

        testCart.getItems().add(cartItem);

        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.singletonList(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productionRepository.findById(10L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cartService.checkoutCart(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void checkoutCart_shouldThrowException_whenInsufficientStockDuringCheckout() {
        // Given
        testProduct.setStock(1);

        CartItem cartItem = CartItem.builder()
                .product(testProduct)
                .quantity(5)
                .cart(testCart)
                .build();

        testCart.getItems().add(cartItem);

        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.singletonList(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productionRepository.findById(10L)).thenReturn(Optional.of(testProduct));

        // When & Then
        assertThatThrownBy(() -> cartService.checkoutCart(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void checkoutCart_shouldCalculateTotalAmountCorrectly_withMultipleItems() {
        // Given
        Product product1 = Product.builder()
                .id(10L)
                .stock(100)
                .price(BigDecimal.valueOf(25))
                .build();

        Product product2 = Product.builder()
                .id(20L)
                .stock(50)
                .price(BigDecimal.valueOf(15))
                .build();

        CartItem item1 = CartItem.builder()
                .product(product1)
                .quantity(2)
                .cart(testCart)
                .build();

        CartItem item2 = CartItem.builder()
                .product(product2)
                .quantity(3)
                .cart(testCart)
                .build();

        testCart.getItems().add(item1);
        testCart.getItems().add(item2);

        when(cartRepository.findActiveCartsByUserId(1L)).thenReturn(Collections.singletonList(testCart));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productionRepository.findById(10L)).thenReturn(Optional.of(product1));
        when(productionRepository.findById(20L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // When
        OrderResponseDto orderResponse = cartService.checkoutCart(1L);

        // Then
        assertThat(orderResponse.getItems()).hasSize(2);
        assertThat(orderResponse.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(95));
    }
}
