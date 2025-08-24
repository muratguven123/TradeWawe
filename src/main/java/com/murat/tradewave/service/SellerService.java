package com.murat.tradewave.service;

import com.murat.tradewave.dto.Seller.SellerRequest;
import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.model.Seller;
import org.springframework.security.core.userdetails.User;

public interface SellerService {
    SellerRequest register(SellerRequest request);
    Seller get(Long id);
    User login(SellerRequest sellerRequest);

}
