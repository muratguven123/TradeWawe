package com.murat.tradewave.service;

import com.murat.tradewave.dto.Seller.SellerLoginRequest;
import com.murat.tradewave.dto.Seller.SellerRegisterRequest;
import com.murat.tradewave.dto.Seller.SellerRequest;
import com.murat.tradewave.dto.Seller.SellerResponse;
import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.model.Seller;
import org.springframework.security.core.userdetails.User;

public interface SellerService {
    SellerResponse register(SellerRegisterRequest request);
    Seller get(Long id);
    User login(SellerLoginRequest sellerRequest);

}
