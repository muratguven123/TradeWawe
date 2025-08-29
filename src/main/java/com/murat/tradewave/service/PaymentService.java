package com.murat.tradewave.service;
import com.murat.tradewave.Enums.OrderStatus;
import com.murat.tradewave.Enums.PaymentStatus;
import com.murat.tradewave.dto.payment.request.PaymentRequest;
import com.murat.tradewave.dto.payment.response.PaymentResponse;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.model.Payment;
import com.murat.tradewave.repository.OrderRepository;
import com.murat.tradewave.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private  Random random;
    @Transactional
    public PaymentResponse initailPayment(PaymentRequest paymentRequest) {
        Order order = orderRepository.findById(paymentRequest.getOrderId()).orElseThrow(()->new RuntimeException("Order not found"));
        if (order.getStatus()== OrderStatus.Created){
            throw new RuntimeException("Order already exists");
        }
        boolean paymentSuccess = random.nextDouble()<0.7;

        PaymentStatus paymentStatus = paymentSuccess? PaymentStatus.Success: PaymentStatus.Failed;
        OrderStatus orderStatus= paymentSuccess ? OrderStatus.Created:OrderStatus.Cancelled;
        order.setStatus(orderStatus);

        Payment payment=Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .status(paymentStatus)
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        orderRepository.save(order);
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .orderId(order.getId())
                .amount(payment.getAmount())
                .paidAt(payment.getPaidAt())
                .build();
    }
    public List<PaymentResponse> paymentGetHistory(String email){
        List<Payment> payments = paymentRepository.findAllByUserEmail(email);

        return payments.stream()
                .map(payment -> PaymentResponse.builder()
                        .paymentId(payment.getId())
                        .orderId(payment.getOrder().getId())
                        .amount(payment.getAmount())
                        .status(payment.getStatus().name())
                        .paidAt(payment.getPaidAt())
                        .build())
                .toList();
    }
















}
