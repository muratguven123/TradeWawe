package com.murat.tradewave.Service;

import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.dto.Seller.SellerLoginRequest;
import com.murat.tradewave.dto.Seller.SellerRegisterRequest;
import com.murat.tradewave.dto.Seller.SellerResponse;
import com.murat.tradewave.model.Seller;
import com.murat.tradewave.model.User;
import com.murat.tradewave.repository.SellerRepository;
import com.murat.tradewave.security.JwtService;
import com.murat.tradewave.service.SellerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;
    @InjectMocks
    private SellerServiceImpl sellerService;

    @BeforeEach
    void setUp() {
        sellerService = new SellerServiceImpl(sellerRepository);
    }

    @Test
    void register_shouldSaveAndReturnSellerResponse_whenEmailNotExists() {
        SellerRegisterRequest request = new SellerRegisterRequest();
        request.setSellerName(" John Doe ");
        request.setSellerEmail("JOHN@example.com ");
        request.setSellerPassword("secret");
        request.setAccountType(Role.COMPANY);

        when(sellerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> {
            Seller s = invocation.getArgument(0);
            s.setId(1L);
            s.setCreatedAt(Instant.now());
            return s;
        });

        SellerResponse response = sellerService.register(request);

        assertEquals(1L, response.getId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());

        verify(sellerRepository).existsByEmail("john@example.com");
        ArgumentCaptor<Seller> captor = ArgumentCaptor.forClass(Seller.class);
        verify(sellerRepository).save(captor.capture());
        Seller savedSeller = captor.getValue();
        assertTrue(new BCryptPasswordEncoder().matches("secret", savedSeller.getPassword()));
    }

    @Test
    void register_shouldThrowDuplicateKeyException_whenEmailExists() {
        SellerRegisterRequest request = new SellerRegisterRequest();
        request.setSellerName("John Doe");
        request.setSellerEmail("john@example.com");
        request.setSellerPassword("secret");
        request.setAccountType(Role.COMPANY);

        when(sellerRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(DuplicateKeyException.class, () -> sellerService.register(request));
        verify(sellerRepository, never()).save(any());
    }

    @Test
    void get_shouldReturnSeller_whenExists() {
        Seller seller = Seller.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("encoded")
                .accountType(Role.COMPANY)
                .build();
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        Seller result = sellerService.get(1L);

        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void get_shouldThrowNoSuchElementException_whenNotFound() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> sellerService.get(1L));
    }

    @Test
    void login_shouldReturnSpringUser() {
        SellerLoginRequest request = new SellerLoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("secret");

        User modelUser = User.builder()
                .email("john@example.com")
                .password("encoded")
                .role(Role.COMPANY)
                .build();
        when(sellerRepository.findByEmail("john@example.com")).thenReturn(modelUser);

        org.springframework.security.core.userdetails.User user = sellerService.login(request);

        assertEquals("john@example.com", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority(Role.COMPANY.toString())));
    }
}
