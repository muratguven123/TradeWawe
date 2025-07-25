package com.murat.tradewave.service;

import com.murat.tradewave.dto.Cart.AddToCartRequest;
import com.murat.tradewave.model.Cart;
import com.murat.tradewave.model.Order;

public interface CartService {
    void addToCart(AddToCartRequest addToCartRequest);
    void removeFromCart(Long userid,Long productid);
    Cart viewCart(Long Userid);
    Order checkoutCart(Long userid);

}
