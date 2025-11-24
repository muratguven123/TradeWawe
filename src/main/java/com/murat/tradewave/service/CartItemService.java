package com.murat.tradewave.service;

import com.murat.tradewave.dto.Cartİtem.AddToCartItem;
import com.murat.tradewave.dto.Cartİtem.DeleteCartItem;
import com.murat.tradewave.dto.product.response.ProductResponse;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.model.CartItem;
import com.murat.tradewave.model.Product;
import com.murat.tradewave.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductServiceImpl productServiceImpl;
    private final Mapper mapper;

    public AddToCartItem saveCartItem(AddToCartItem addToCartItem) {
        ProductResponse product = productServiceImpl.getProductV2(addToCartItem.getProduct());
        Product product1 = mapper.MapToEntityForProduct(product);
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product1);
        cartItemRepository.save(cartItem);
        return addToCartItem;
    }

    public DeleteCartItem saveDeleteCartItem(DeleteCartItem deleteCartItem) {
        CartItem cartItem = cartItemRepository.findCartItemsById(deleteCartItem.getProduct().getId());
        cartItemRepository.delete(cartItem);
        return deleteCartItem;
    }
}
