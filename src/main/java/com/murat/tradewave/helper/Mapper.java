package com.murat.tradewave.helper;

import com.murat.tradewave.dto.Address.response.AdressResponse;
import com.murat.tradewave.dto.category.response.CategoryResponse;
import com.murat.tradewave.dto.product.response.ProductResponse;
import com.murat.tradewave.model.Address;
import com.murat.tradewave.model.Category;
import com.murat.tradewave.model.EmbeddedAddress;
import com.murat.tradewave.model.Product;
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

}
