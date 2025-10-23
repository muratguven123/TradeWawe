package com.murat.tradewave.service;

import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.dto.Cart.RemoveCartRequest;
import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.CartItem;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.model.OrderItem;
import com.murat.tradewave.model.Product;
import com.murat.tradewave.repository.CartItemRepository;
import com.murat.tradewave.repository.CartRepository;
import com.murat.tradewave.repository.ProductionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductionRepository productionRepository;

    @Override
    @Transactional
    public void addToCart(AddToCartRequest addToCartRequest) {
        Product product = productionRepository.findById(addToCartRequest.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + addToCartRequest.getProductId()));

        if (product.getStock() < addToCartRequest.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }

        Cart cart = cartRepository.findCartByUserId(addToCartRequest.getUserId()).orElseGet(() -> {
            Cart newCart = Cart.builder()
                    .userId(addToCartRequest.getUserId())
                    .items(new ArrayList<>())
                    .checkedout(false)
                    .build();
            return cartRepository.save(newCart);
        });

        Optional<CartItem> optionalCartItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(addToCartRequest.getProductId()))
                .findFirst();

        if (optionalCartItem.isPresent()) {
            CartItem existingCartItem = optionalCartItem.get();
            int newQuantity = existingCartItem.getQuantity() + addToCartRequest.getQuantity();

            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
            }

            existingCartItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(addToCartRequest.getProductId())
                    .quantity(addToCartRequest.getQuantity())
                    .build();

            cart.getItems().add(newItem);
        }
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(RemoveCartRequest removeCartRequest) {
        Cart cart = cartRepository.findCartByUserId(removeCartRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + removeCartRequest.getUserId()));

        CartItem itemRemove = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(removeCartRequest.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart: " + removeCartRequest.getProductId()));

        cart.getItems().remove(itemRemove);
        cartItemRepository.delete(itemRemove);

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart viewCart(Long userId) {
        return cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Override
    @Transactional
    public Order checkoutCart(Long userId) {
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    Product product = productionRepository.findById(cartItem.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));

                    if (product.getStock() < cartItem.getQuantity()) {
                        throw new RuntimeException("Insufficient stock for product: " + product.getName()
                                + ". Available: " + product.getStock());
                    }

                    return OrderItem.builder()
                            .product(product)
                            .quantity(cartItem.getQuantity())
                            .price(product.getPrice())
                            .build();
                })
                .toList();

        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .items(orderItems)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .build();

        order.getItems().forEach(item -> item.setOrder(order));

        cart.setCheckedout(true);
        cartRepository.save(cart);

        return order;
    }
}
