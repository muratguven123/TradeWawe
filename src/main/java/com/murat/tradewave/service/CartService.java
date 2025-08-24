package com.murat.tradewave.service;

import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.dto.Cart.RemoveCartRequest;
import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.Order;
import jakarta.transaction.Transactional;

public interface CartService {
    void addToCart(AddToCartRequest addToCartRequest);


    @Transactional
    void removeFromCart(RemoveCartRequest removeCartRequest);

    Cart viewCart(Long Userid);
    Order checkoutCart(Long userid);

}
