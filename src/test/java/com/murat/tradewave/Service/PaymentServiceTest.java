package com.murat.tradewave.Service;

import com.murat.tradewave.Enums.OrderStatus;
import com.murat.tradewave.Enums.PaymentStatus;
import com.murat.tradewave.dto.payment.request.PaymentRequest;
import com.murat.tradewave.dto.payment.response.PaymentResponse;
import com.murat.tradewave.exception.OrderAlreadyPaidException;
import com.murat.tradewave.exception.OrderNotFoundException;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.model.Payment;
import com.murat.tradewave.repository.OrderRepository;
import com.murat.tradewave.repository.PaymentRepository;
import com.murat.tradewave.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Tests - Edge Cases & Error Scenarios")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest paymentRequest;
    private Order order;

    @BeforeEach
    void setUp() {
        paymentRequest = PaymentRequest.builder()
                .orderId(1L)
                .build();

        order = Order.builder()
                .id(1L)
                .status(OrderStatus.Created)
                .totalAmount(new BigDecimal("150.00"))
                .build();
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order does not exist")
    void initailPayment_ShouldThrowException_WhenOrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrderNotFoundException.class,
            () -> paymentService.initailPayment(paymentRequest),
            "Order not found");

        verify(orderRepository).findById(1L);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Should throw OrderAlreadyPaidException when order status is not Created")
    void initailPayment_ShouldThrowException_WhenOrderAlreadyPaid() {
        // Given
        order.setStatus(OrderStatus.Paid);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When & Then
        assertThrows(OrderAlreadyPaidException.class,
            () -> paymentService.initailPayment(paymentRequest),
            "Order already exists");

        verify(orderRepository).findById(1L);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Should throw exception when order is Cancelled")
    void initailPayment_ShouldThrowException_WhenOrderIsCancelled() {
        // Given
        order.setStatus(OrderStatus.Cancelled);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When & Then
        assertThrows(OrderAlreadyPaidException.class,
            () -> paymentService.initailPayment(paymentRequest));

        verify(orderRepository).findById(1L);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Should throw exception when order is in Pending status")
    void initailPayment_ShouldThrowException_WhenOrderIsPending() {
        // Given
        order.setStatus(OrderStatus.Pending);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When & Then
        assertThrows(OrderAlreadyPaidException.class,
            () -> paymentService.initailPayment(paymentRequest));

        verify(orderRepository).findById(1L);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    @DisplayName("Should process payment and return response with proper data")
    void initailPayment_ShouldProcessPayment_WhenOrderIsValid() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(100L);
            return payment;
        });
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        PaymentResponse response = paymentService.initailPayment(paymentRequest);

        // Then
        assertNotNull(response);
        assertEquals(100L, response.getPaymentId());
        assertEquals(1L, response.getOrderId());
        assertEquals(new BigDecimal("150.00"), response.getAmount());
        assertNotNull(response.getPaidAt());
        assertTrue(response.getStatus().equals("Success") || response.getStatus().equals("Failed"));

        verify(orderRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order status to Paid or Cancelled based on payment result")
    void initailPayment_ShouldUpdateOrderStatus() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(100L);
            return payment;
        });
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        paymentService.initailPayment(paymentRequest);

        // Then
        assertTrue(
            order.getStatus() == OrderStatus.Paid || order.getStatus() == OrderStatus.Cancelled,
            "Order status should be either Paid or Cancelled"
        );
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should create payment with correct order reference and amount")
    void initailPayment_ShouldCreatePaymentWithCorrectData() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            assertEquals(order, payment.getOrder());
            assertEquals(new BigDecimal("150.00"), payment.getAmount());
            assertNotNull(payment.getPaidAt());
            assertTrue(payment.getStatus() == PaymentStatus.Success ||
                      payment.getStatus() == PaymentStatus.Failed);
            payment.setId(100L);
            return payment;
        });
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        paymentService.initailPayment(paymentRequest);

        // Then
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should return empty list when user has no payment history")
    void paymentGetHistory_ShouldReturnEmptyList_WhenNoPayments() {
        // Given
        String email = "user@example.com";
        when(paymentRepository.findAllByUserEmail(email)).thenReturn(Collections.emptyList());

        // When
        List<PaymentResponse> responses = paymentService.paymentGetHistory(email);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(paymentRepository).findAllByUserEmail(email);
    }

    @Test
    @DisplayName("Should return payment history with correct mapping")
    void paymentGetHistory_ShouldReturnMappedResponses_WhenPaymentsExist() {
        // Given
        String email = "user@example.com";
        Order order1 = Order.builder().id(1L).build();
        Order order2 = Order.builder().id(2L).build();

        Payment payment1 = Payment.builder()
                .id(100L)
                .order(order1)
                .amount(new BigDecimal("50.00"))
                .status(PaymentStatus.Success)
                .paidAt(LocalDateTime.now())
                .build();

        Payment payment2 = Payment.builder()
                .id(101L)
                .order(order2)
                .amount(new BigDecimal("75.00"))
                .status(PaymentStatus.Failed)
                .paidAt(LocalDateTime.now())
                .build();

        when(paymentRepository.findAllByUserEmail(email))
                .thenReturn(List.of(payment1, payment2));

        // When
        List<PaymentResponse> responses = paymentService.paymentGetHistory(email);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        PaymentResponse response1 = responses.get(0);
        assertEquals(100L, response1.getPaymentId());
        assertEquals(1L, response1.getOrderId());
        assertEquals(new BigDecimal("50.00"), response1.getAmount());
        assertEquals("Success", response1.getStatus());
        assertNotNull(response1.getPaidAt());

        PaymentResponse response2 = responses.get(1);
        assertEquals(101L, response2.getPaymentId());
        assertEquals(2L, response2.getOrderId());
        assertEquals(new BigDecimal("75.00"), response2.getAmount());
        assertEquals("Failed", response2.getStatus());
        assertNotNull(response2.getPaidAt());

        verify(paymentRepository).findAllByUserEmail(email);
    }

    @Test
    @DisplayName("Should handle null email in payment history")
    void paymentGetHistory_ShouldHandleNullEmail() {
        // Given
        when(paymentRepository.findAllByUserEmail(null)).thenReturn(Collections.emptyList());

        // When
        List<PaymentResponse> responses = paymentService.paymentGetHistory(null);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(paymentRepository).findAllByUserEmail(null);
    }

    @Test
    @DisplayName("Should handle large payment amounts correctly")
    void initailPayment_ShouldHandleLargeAmounts() {
        // Given
        order.setTotalAmount(new BigDecimal("999999.99"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(100L);
            return payment;
        });
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        PaymentResponse response = paymentService.initailPayment(paymentRequest);

        // Then
        assertNotNull(response);
        assertEquals(new BigDecimal("999999.99"), response.getAmount());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should handle payment request with null orderId")
    void initailPayment_ShouldThrowException_WhenOrderIdIsNull() {
        // Given
        PaymentRequest invalidRequest = PaymentRequest.builder()
                .orderId(null)
                .build();
        when(orderRepository.findById(null)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(OrderNotFoundException.class,
            () -> paymentService.initailPayment(invalidRequest));

        verify(orderRepository).findById(null);
    }
}