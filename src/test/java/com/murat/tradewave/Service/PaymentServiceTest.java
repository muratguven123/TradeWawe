package com.murat.tradewave.Service;

import com.murat.tradewave.Enums.OrderStatus;
import com.murat.tradewave.Enums.PaymentStatus;
import com.murat.tradewave.dto.payment.request.PaymentRequest;
import com.murat.tradewave.dto.payment.response.PaymentResponse;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.model.Payment;
import com.murat.tradewave.repository.OrderRepository;
import com.murat.tradewave.repository.PaymentRepository;
import com.murat.tradewave.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private Random random;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void initailPayment_shouldProcessSuccessfulPayment() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.Pending)
                .totalAmount(BigDecimal.TEN)
                .build();
        PaymentRequest request = PaymentRequest.builder()
                .orderId(1L)
                .amount(BigDecimal.TEN)
                .status("Pending")
                .paidAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(10L);
            return p;
        });
        when(orderRepository.save(order)).thenReturn(order);
        when(random.nextDouble()).thenReturn(0.5);

        PaymentResponse response = paymentService.initailPayment(request);

        assertEquals(10L, response.getPaymentId());
        assertEquals("Success", response.getStatus());
        assertEquals(OrderStatus.Created, order.getStatus());
        assertEquals(BigDecimal.TEN, response.getAmount());
        assertNotNull(response.getPaidAt());

        verify(orderRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository).save(order);
        verify(random).nextDouble();
    }

    @Test
    void initailPayment_shouldProcessFailedPayment() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.Pending)
                .totalAmount(BigDecimal.TEN)
                .build();
        PaymentRequest request = PaymentRequest.builder()
                .orderId(1L)
                .amount(BigDecimal.TEN)
                .status("Pending")
                .paidAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(11L);
            return p;
        });
        when(orderRepository.save(order)).thenReturn(order);
        when(random.nextDouble()).thenReturn(0.8);

        PaymentResponse response = paymentService.initailPayment(request);

        assertEquals("Failed", response.getStatus());
        assertEquals(OrderStatus.Cancelled, order.getStatus());
        assertEquals(BigDecimal.TEN, response.getAmount());
        assertNotNull(response.getPaidAt());

        verify(orderRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
        verify(orderRepository).save(order);
        verify(random).nextDouble();
    }

    @Test
    void initailPayment_shouldThrowException_whenOrderAlreadyCreated() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.Created)
                .totalAmount(BigDecimal.TEN)
                .build();
        PaymentRequest request = PaymentRequest.builder()
                .orderId(1L)
                .amount(BigDecimal.TEN)
                .status("Pending")
                .paidAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> paymentService.initailPayment(request));

        verify(orderRepository).findById(1L);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(random);
    }

    @Test
    void paymentGetHistory_shouldReturnMappedResponses() {
        Order order1 = Order.builder().id(1L).build();
        Order order2 = Order.builder().id(2L).build();
        Payment payment1 = Payment.builder()
                .id(100L)
                .order(order1)
                .amount(BigDecimal.ONE)
                .status(PaymentStatus.Success)
                .paidAt(LocalDateTime.now())
                .build();
        Payment payment2 = Payment.builder()
                .id(101L)
                .order(order2)
                .amount(BigDecimal.TEN)
                .status(PaymentStatus.Failed)
                .paidAt(LocalDateTime.now())
                .build();

        when(paymentRepository.findAllByUserEmail("user@example.com"))
                .thenReturn(List.of(payment1, payment2));

        List<PaymentResponse> responses = paymentService.paymentGetHistory("user@example.com");

        assertEquals(2, responses.size());
        assertEquals(100L, responses.get(0).getPaymentId());
        assertEquals(1L, responses.get(0).getOrderId());
        assertEquals("Success", responses.get(0).getStatus());
        assertEquals(101L, responses.get(1).getPaymentId());
        assertEquals("Failed", responses.get(1).getStatus());

        verify(paymentRepository).findAllByUserEmail("user@example.com");
    }
}