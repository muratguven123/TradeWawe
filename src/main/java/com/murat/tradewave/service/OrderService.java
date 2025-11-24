package com.murat.tradewave.service;

import com.murat.tradewave.dto.Order.request.OrderRequest;
import com.murat.tradewave.dto.Order.response.OrderResponse;
import com.murat.tradewave.dto.OrderItem.Request.OrderItemRequest;
import com.murat.tradewave.dto.OrderItem.Response.OrderItemResponse;
import com.murat.tradewave.Enums.OrderStatus;
import com.murat.tradewave.exception.AccessDeniedException;
import com.murat.tradewave.exception.OrderNotFoundException;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.model.OrderItem;
import com.murat.tradewave.model.Product;
import com.murat.tradewave.model.User;
import com.murat.tradewave.repository.OrderRepository;
import com.murat.tradewave.repository.ProductionRepository;
import com.murat.tradewave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductionRepository productionRepository;
    private final UserRepository userRepository;


    public OrderRequest createOrder(OrderItemRequest orderRequest, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal totalPrice = BigDecimal.ZERO;


            Product product = productionRepository.findById(orderRequest.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(orderRequest.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(orderRequest.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);

        Order order = Order.builder()
                .user(user)
                .items(new ArrayList<>())
                .status(OrderStatus.Created)
                .totalAmount(totalPrice)
                .createdAt(LocalDateTime.now())
                .build();

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        List<OrderItemResponse> itemResponses = savedOrder.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getProduct().getPrice())
                        .build())
                .toList();

        return OrderRequest.builder()
                .orderId(savedOrder.getId())
                .status(savedOrder.getStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .items(itemResponses)
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }
@Transactional(readOnly = true)
    public OrderResponse getOrderDetail(Long orderId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getProduct().getPrice())
                        .build())
                .toList();
        return OrderResponse.builder()
                .orderId(order.getId())
                .createdAt(order.getCreatedAt())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .items(items)
                .build();
    }
}
