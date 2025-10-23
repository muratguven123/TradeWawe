package com.murat.tradewave.service;
import com.murat.tradewave.dto.Seller.SellerLoginRequest;
import com.murat.tradewave.dto.Seller.SellerRegisterRequest;
import com.murat.tradewave.dto.Seller.SellerResponse;
import com.murat.tradewave.model.Seller;
import com.murat.tradewave.repository.SellerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Transactional
    public SellerResponse register(@Valid SellerRegisterRequest request) {
        if (sellerRepository.existsByEmail(request.getSellerEmail().trim().toLowerCase())) {
            throw new DuplicateKeyException("Seller already exists with this email");
        }
        String hashed = bCryptPasswordEncoder.encode(request.getSellerPassword());
        Seller seller = Seller.builder()
                .accountType(request.getAccountType())
                .name(request.getSellerName().trim())
                .email(request.getSellerEmail().trim().toLowerCase())
                .password(hashed)
                .build();

        Seller saved = sellerRepository.save(seller);

      return SellerResponse.builder()
              .name(seller.getName())
              .id(seller.getId())
              .createdAt(seller.getCreatedAt())
              .email(seller.getEmail())
              .build();
    }
    @Override
    public Seller get(Long id) {
        return sellerRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
    @Override
    public org.springframework.security.core.userdetails.User login(SellerLoginRequest sellerRequest) {
        Seller seller = sellerRepository.findByEmail(sellerRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        if (!bCryptPasswordEncoder.matches(sellerRequest.getPassword(), seller.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new org.springframework.security.core.userdetails.User(
                seller.getEmail(),
                seller.getPassword(),
                List.of(new SimpleGrantedAuthority(seller.getAccountType().toString()))
        );

    }
}
