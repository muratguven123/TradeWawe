package com.murat.tradewave.service;

import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.dto.Cart.RemoveCartRequest;
import com.murat.tradewave.dto.Cartİtem.ViewCartItems;

import com.murat.tradewave.Enums.OrderStatus;
import com.murat.tradewave.exception.CartException;
import com.murat.tradewave.exception.InsufficientStockException;
import com.murat.tradewave.exception.ProductNotFoundException;
import com.murat.tradewave.exception.UserNotFoundException;
import com.murat.tradewave.model.*;
import com.murat.tradewave.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductionRepository productionRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public void addToCart(AddToCartRequest addToCartRequest) {
        // Ürünü bul
        Product product = productionRepository.findById(addToCartRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + addToCartRequest.getProductId()));

        // Stok kontrolü
        if (product.getStock() < addToCartRequest.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock. Available: " + product.getStock());
        }

        // Kullanıcıyı bul
        User user = userRepository.findById(addToCartRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + addToCartRequest.getUserId()));

        // Kullanıcı için aktif cart'ları getir
        List<Cart> activeCarts = cartRepository.findActiveCartsByUserId(addToCartRequest.getUserId());
        Cart cart;

        if (activeCarts.isEmpty()) {
            // Hiç aktif sepet yoksa otomatik yeni sepet oluştur
            cart = Cart.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .checkedout(false)
                    .build();
            cart = cartRepository.save(cart);
        } else {
            // Bir veya birden fazla aktif sepet var
            cart = activeCarts.get(0); // ilkini kullan

            if (activeCarts.size() > 1) {
                // Geri kalanları kapat (checkedout = true) – veri bütünlüğü için
                for (int i = 1; i < activeCarts.size(); i++) {
                    Cart extraCart = activeCarts.get(i);
                    extraCart.setCheckedout(true);
                    cartRepository.save(extraCart);
                }
            }
        }

        // Bu sepette ilgili ürünü bul veya yeni ekle
        CartItem existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getId().equals(addToCartRequest.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingCartItem != null) {
            int newQuantity = existingCartItem.getQuantity() + addToCartRequest.getQuantity();

            if (product.getStock() < newQuantity) {
                throw new InsufficientStockException("Insufficient stock. Available: " + product.getStock());
            }

            existingCartItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(addToCartRequest.getQuantity())
                    .build();

            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(RemoveCartRequest removeCartRequest) {
        List<Cart> activeCarts = cartRepository.findActiveCartsByUserId(removeCartRequest.getUserId());
        if (activeCarts.isEmpty()) {
            throw new RuntimeException("Cart not found for user: " + removeCartRequest.getUserId());
        }
        Cart cart = activeCarts.get(0);

        CartItem itemRemove = cart.getItems().stream()
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getId().equals(removeCartRequest.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart: " + removeCartRequest.getProductId()));

        cart.getItems().remove(itemRemove);
        cartItemRepository.delete(itemRemove);

        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart viewCart(Long userId) {
        List<Cart> activeCarts = cartRepository.findActiveCartsByUserId(userId);
        if (activeCarts.isEmpty()) {
            throw new CartException("Cart not found for user: " + userId);
        }
        Cart cart = activeCarts.get(0);

        // Force load items to avoid lazy loading issue
        cart.getItems().size();
        return cart;
    }

    @Override
    @Transactional
    public ViewCartItems viewCartV2(ViewCartItems viewCartItems) {
        return cartRepository.findByIdWithItems(viewCartItems.getCartId())
                .map(cart -> {
                    List<com.murat.tradewave.dto.Cartİtem.CartItemDto> itemDtos = cart.getItems().stream()
                            .filter(item -> item.getProduct() != null)
                            .map(item -> {
                                BigDecimal subtotal = item.getProduct().getPrice()
                                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                                return com.murat.tradewave.dto.Cartİtem.CartItemDto.builder()
                                        .id(item.getId())
                                        .productId(item.getProduct().getId())
                                        .productName(item.getProduct().getName())
                                        .productPrice(item.getProduct().getPrice())
                                        .productImageUrl(item.getProduct().getImageUrl())
                                        .quantity(item.getQuantity())
                                        .subtotal(subtotal)
                                        .build();
                            })
                            .toList();

                    BigDecimal totalAmount = itemDtos.stream()
                            .map(com.murat.tradewave.dto.Cartİtem.CartItemDto::getSubtotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    Integer totalItems = itemDtos.stream()
                            .mapToInt(com.murat.tradewave.dto.Cartİtem.CartItemDto::getQuantity)
                            .sum();

                    return ViewCartItems.builder()
                            .cartId(cart.getId())
                            .items(itemDtos)
                            .totalAmount(totalAmount)
                            .totalItems(totalItems)
                            .build();
                })
                .orElseThrow(() -> new CartException("Cart not found for the given items"));
    }

    @Override
    @Transactional
    public ViewCartItems viewCartByUserId(Long userId) {
        List<Cart> activeCarts = cartRepository.findActiveCartsByUserId(userId);

        if (activeCarts.isEmpty()) {
            // Sepet yoksa boş sepet dön
            return ViewCartItems.builder()
                    .cartId(null)
                    .items(new ArrayList<>())
                    .totalAmount(BigDecimal.ZERO)
                    .totalItems(0)
                    .build();
        }

        Cart cart = activeCarts.get(0);

        List<com.murat.tradewave.dto.Cartİtem.CartItemDto> itemDtos = cart.getItems().stream()
                .filter(item -> item.getProduct() != null)
                .map(item -> {
                    BigDecimal subtotal = item.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    return com.murat.tradewave.dto.Cartİtem.CartItemDto.builder()
                            .id(item.getId())
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .productPrice(item.getProduct().getPrice())
                            .productImageUrl(item.getProduct().getImageUrl())
                            .quantity(item.getQuantity())
                            .subtotal(subtotal)
                            .build();
                })
                .toList();

        BigDecimal totalAmount = itemDtos.stream()
                .map(com.murat.tradewave.dto.Cartİtem.CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = itemDtos.stream()
                .mapToInt(com.murat.tradewave.dto.Cartİtem.CartItemDto::getQuantity)
                .sum();

        return ViewCartItems.builder()
                .cartId(cart.getId())
                .items(itemDtos)
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .build();
    }

    @Override
    @Transactional
    public com.murat.tradewave.dto.Order.OrderResponseDto checkoutCart(Long userId) {
        List<Cart> activeCarts = cartRepository.findActiveCartsByUserId(userId);
        if (activeCarts.isEmpty()) {
            throw new RuntimeException("Cart not found for user: " + userId);
        }
        Cart cart = activeCarts.get(0);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<OrderItem> orderItems = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProduct() != null)  // Null product kontrolü
                .map(cartItem -> {
                    Product product = productionRepository.findById(cartItem.getProduct().getId())
                            .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProduct().getId()));

                    if (product.getStock() < cartItem.getQuantity()) {
                        throw new RuntimeException("Insufficient stock for product: " + product.getName()
                                + ". Available: " + product.getStock());
                    }

                    // Stok düşür
                    product.setStock(product.getStock() - cartItem.getQuantity());
                    productionRepository.save(product);

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
                .user(user)
                .items(orderItems)
                .status(OrderStatus.Created)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .build();

        order.getItems().forEach(item -> item.setOrder(order));

        // Order'ı kaydet
        Order savedOrder = orderRepository.save(order);

        cart.setCheckedout(true);
        cartRepository.save(cart);

        // Entity'yi DTO'ya çevir (transaction içinde)
        List<com.murat.tradewave.dto.Order.OrderItemDto> orderItemDtos = savedOrder.getItems().stream()
                .map(item -> com.murat.tradewave.dto.Order.OrderItemDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return com.murat.tradewave.dto.Order.OrderResponseDto.builder()
                .orderId(savedOrder.getId())
                .items(orderItemDtos)
                .status(savedOrder.getStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .createdAt(savedOrder.getCreatedAt())
                .userId(savedOrder.getUser().getId())
                .build();
    }
}
