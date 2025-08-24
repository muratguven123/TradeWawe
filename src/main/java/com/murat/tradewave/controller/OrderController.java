package com.murat.tradewave.controller;

import com.murat.tradewave.dto.Order.request.OrderRequest;
import com.murat.tradewave.dto.Order.response.OrderResponse;
import com.murat.tradewave.dto.OrderItem.Request.OrderItemRequest;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable Long id, Authentication authentication) throws AccessDeniedException {
        String email = authentication.name();
        OrderResponse response = orderService.getOrderDetail(id, email);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<OrderRequest> createOrder(@RequestBody OrderItemRequest request,
                                                     @AuthenticationPrincipal(expression = "username") String email) {
        OrderRequest orderRequest = orderService.createOrder(request, email);
        return ResponseEntity.ok(orderRequest);
    }
}
