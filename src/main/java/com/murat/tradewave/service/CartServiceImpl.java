package com.murat.tradewave.service;

import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.CartItem;
import com.murat.tradewave.model.Order;
import com.murat.tradewave.model.OrderItem;
import com.murat.tradewave.repository.CartItemRepository;
import com.murat.tradewave.repository.CartRepository;
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
public class CartServiceImpl implements CartService{
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    @Override
    @Transactional
    public void addToCart(AddToCartRequest addToCartRequest) {
        Cart cart = cartRepository.findByUserIdAndCheckedOutFalse(addToCartRequest.getUserid()).orElseGet(()-> {
            Cart newCart = Cart.builder()
                    .userid(addToCartRequest.getUserid())
                    .items(new ArrayList<>())
                    .checkedout(false)
                    .build();
            return cartRepository.save(newCart);
        });
        Optional<CartItem> optionalCartItem=cart.getItems().stream().filter(item->item.getProductId().equals(addToCartRequest.getProductid())).findFirst();
        if(optionalCartItem.isPresent()){
            CartItem existingCartItem=optionalCartItem.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity()+addToCartRequest.getQuantity());
        }else{
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(addToCartRequest.getProductid())
                    .quantity(addToCartRequest.getQuantity())
                    .build();

            cart.getItems().add(newItem);
        }
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(Long userid, Long productid) {
        Cart cart = cartRepository.findByUserIdAndCheckedOutFalse(userid).orElseThrow(() -> new RuntimeException("Cart is not founded"));

        CartItem itemRemove = cart.getItems().stream().filter(item->item.getProductId().equals(productid)).findFirst().orElse(null);

        cart.getItems().remove(itemRemove);
        cartItemRepository.delete(itemRemove);

        cartRepository.save(cart);
    }
    @Override
    @Transactional()
    public Cart viewCart(Long userid) {
        return cartRepository.findByUserIdAndCheckedOutFalse(userid)
                .orElseThrow(() -> new RuntimeException("Cart is not founded"));

    }

    @Override
    @Transactional
    public Order checkoutCart(Long userid) {
        Cart cart=cartRepository.findByUserIdAndCheckedOutFalse(userid).orElseThrow(() -> new RuntimeException("Cart is not founded"));

        if(cart.getItems().isEmpty()){
            throw new RuntimeException("Cart is empty");
        }
        List<OrderItem> orderItems=cart.getItems().stream()
                 .map(cartItem->OrderItem.builder()
                         .id(cartItem.getProductId())
                         .quantity(cartItem.getQuantity())
                         .price(fetchProductPrice(cartItem.getProductId()))
                         .build())
                .toList();

        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        Order order = Order.builder()
                .id(userid)
                .items(orderItems)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .build();
        order.getItems().forEach(item -> item.setOrder(order));
        cart.setCheckedout(true);
        cartRepository.save(cart);

        return order;
    }
    private BigDecimal fetchProductPrice(Long productId) {
        return BigDecimal.valueOf(productId);
    }
}
