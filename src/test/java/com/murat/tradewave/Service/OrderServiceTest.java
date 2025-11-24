package com.murat.tradewave.Service;
import com.murat.tradewave.Enums.OrderStatus;
import com.murat.tradewave.dto.Order.request.OrderRequest;
import com.murat.tradewave.dto.Order.response.OrderResponse;
import com.murat.tradewave.dto.OrderItem.Request.OrderItemRequest;
import com.murat.tradewave.dto.OrderItem.Response.OrderItemResponse;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.model.OrderItem;
import com.murat.tradewave.model.Product;
import com.murat.tradewave.model.User;
import com.murat.tradewave.repository.OrderRepository;
import com.murat.tradewave.repository.ProductionRepository;
import com.murat.tradewave.repository.UserRepository;
import com.murat.tradewave.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductionRepository productionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldBuildAndSaveOrder() {
        String email = "user@example.com";
        User user = User.builder().id(1L).email(email).build();
        Product product = Product.builder().id(1L).name("Prod").price(BigDecimal.TEN).build();
        OrderItemRequest itemRequest = OrderItemRequest.builder()
                .productıd(1L)
                .quantity(2)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productionRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(5L);
            return order;
        });

        OrderRequest result = orderService.createOrder(itemRequest, email);

        assertEquals(5L, result.getOrderId());
        assertEquals(OrderStatus.Created, result.getStatus());
        assertEquals(BigDecimal.valueOf(20), result.getTotalAmount());
        assertEquals(1, result.getItems().size());
        OrderItem savedItem = result.getItems().get(0);
        assertEquals(product, savedItem.getProduct());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(product.getPrice(), savedItem.getPrice());

        verify(userRepository).findByEmail(email);
        verify(productionRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrderDetail_shouldReturnResponse_whenUserOwnsOrder() throws AccessDeniedException {
        String email = "user@example.com";
        User user = User.builder().id(1L).email(email).build();
        Product product = Product.builder().id(1L).name("Prod").price(BigDecimal.TEN).build();
        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .price(BigDecimal.TEN)
                .build();
        Order order = Order.builder()
                .id(10L)
                .user(user)
                .items(List.of(item))
                .status(OrderStatus.Created)
                .totalAmount(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .build();
        item.setOrder(order);

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderDetail(10L, email);

        assertEquals(10L, response.getOrderId());
        assertEquals("Created", response.getStatus());
        assertEquals(BigDecimal.valueOf(20), response.getTotalAmount());
        assertEquals(1, response.getItems().size());
        OrderItemResponse respItem = response.getItems().get(0);
        assertEquals("Prod", respItem.getProductName());
        assertEquals(2, respItem.getQuantity());
        assertEquals(product.getPrice(), respItem.getPrice());

        verify(orderRepository).findById(10L);
    }

    @Test
    void getOrderDetail_shouldThrowAccessDenied_whenEmailMismatch() {
        User user = User.builder().id(1L).email("owner@example.com").build();
        Order order = Order.builder()
                .id(10L)
                .user(user)
                .items(List.of())
                .status(OrderStatus.Created)
                .totalAmount(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class,
                () -> orderService.getOrderDetail(10L, "other@example.com"));

        verify(orderRepository).findById(10L);
    }
}

