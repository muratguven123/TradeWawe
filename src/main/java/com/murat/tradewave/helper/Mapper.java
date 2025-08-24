package com.murat.tradewave.helper;

import com.murat.tradewave.dto.Address.response.AdressResponse;
import com.murat.tradewave.dto.Seller.SellerRequest;
import com.murat.tradewave.dto.category.response.CategoryResponse;
import com.murat.tradewave.dto.product.response.ProductResponse;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.model.*;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
    public CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }
    public AdressResponse mapToResponse(Address address) {
        return AdressResponse.builder()
                .id(address.getId())
                .addresName(address.getName())
                .title(address.getTitle())
                .discrit(address.getDiscrict())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .street(address.getStreet())
                .build();
    }

    public EmbeddedAddress convertToEmbedded(Address address) {
        return EmbeddedAddress.builder()
                .title(address.getTitle())
                .street(address.getStreet())
                .city(address.getCity())
                .district(address.getDiscrict())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();
    }
public UserResponse mapToUserResponse(User user){
        return UserResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .id(user.getId())
                .password(user.getPassword())
                .build();
}
public SellerRequest mapToSellerResponse(Seller seller){
       return SellerRequest.builder()
                .sellerName(seller.getName())
                .sellerPassword(seller.getPassword())
                .sellerEmail(seller.getEmail())
                .build();
}

}
