package com.murat.tradewave.service;

import com.murat.tradewave.dto.Seller.SellerRequest;
import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.model.Seller;
import com.murat.tradewave.repository.SellerRepository;
import com.murat.tradewave.security.JwtService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor

public class SellerServiceImpl implements SellerService {
    private Mapper mapper;
    private final SellerRepository sellerRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Override
    public SellerRequest register(SellerRequest request) {
        if (sellerRepository.findById(request.getSellerid()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        Seller seller = Seller.builder()
                .password(String.valueOf(Long.valueOf(bCryptPasswordEncoder.encode(request.getSellerPassword()))))
                .name(request.getSellerName())
                .build();
        sellerRepository.save(seller);
        String token = jwtService.generateToken(seller.getEmail());
        return SellerRequest.builder()
                .Sellerid(seller.getId())
                .sellerName(seller.getName())
                .token(token)
                .build();
    }
    @Override
    public Seller get(Long id) {
        return sellerRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
    @Override
    public org.springframework.security.core.userdetails.User login(SellerRequest sellerRequest) {
        com.murat.tradewave.model.User user = sellerRepository.findByEmail(sellerRequest.getSellerEmail());
        return new org.springframework.security.core.userdetails.User(
                sellerRequest.getSellerEmail(),
                sellerRequest.getSellerPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
        );

    }
}
