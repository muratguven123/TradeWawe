package com.murat.tradewave.Service;

import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.dto.Seller.SellerLoginRequest;
import com.murat.tradewave.dto.Seller.SellerRegisterRequest;
import com.murat.tradewave.dto.Seller.SellerResponse;
import com.murat.tradewave.model.Seller;
import com.murat.tradewave.repository.SellerRepository;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerServiceImpl sellerService;

    private SellerRegisterRequest registerRequest;
    private SellerLoginRequest loginRequest;
    private Seller testSeller;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // Setup register request
        registerRequest = new SellerRegisterRequest();
        registerRequest.setSellerName("John Doe");
        registerRequest.setSellerEmail("john@example.com");
        registerRequest.setSellerPassword("password123");
        registerRequest.setAccountType(Role.COMPANY);

        // Setup login request
        loginRequest = new SellerLoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        // Setup test seller
        testSeller = Seller.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password(passwordEncoder.encode("password123"))
                .accountType(Role.COMPANY)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void register_shouldSaveSellerAndReturnResponse_whenEmailDoesNotExist() {
        // Given
        when(sellerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> {
            Seller seller = invocation.getArgument(0);
            seller.setId(1L);
            seller.setCreatedAt(Instant.now());
            return seller;
        });

        // When
        SellerResponse response = sellerService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("John Doe");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getCreatedAt()).isNotNull();

        verify(sellerRepository).existsByEmail("john@example.com");

        ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);
        verify(sellerRepository).save(sellerCaptor.capture());

        Seller savedSeller = sellerCaptor.getValue();
        assertThat(savedSeller.getName()).isEqualTo("John Doe");
        assertThat(savedSeller.getEmail()).isEqualTo("john@example.com");
        assertThat(savedSeller.getAccountType()).isEqualTo(Role.COMPANY);
        assertTrue(passwordEncoder.matches("password123", savedSeller.getPassword()));
    }

    @Test
    void register_shouldTrimAndLowercaseEmail() {
        // Given
        registerRequest.setSellerEmail("  JOHN@EXAMPLE.COM  ");
        registerRequest.setSellerName("  John Doe  ");

        when(sellerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> {
            Seller seller = invocation.getArgument(0);
            seller.setId(1L);
            seller.setCreatedAt(Instant.now());
            return seller;
        });

        // When
        SellerResponse response = sellerService.register(registerRequest);

        // Then
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        assertThat(response.getName()).isEqualTo("John Doe");

        verify(sellerRepository).existsByEmail("john@example.com");

        ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);
        verify(sellerRepository).save(sellerCaptor.capture());

        Seller savedSeller = sellerCaptor.getValue();
        assertThat(savedSeller.getEmail()).isEqualTo("john@example.com");
        assertThat(savedSeller.getName()).isEqualTo("John Doe");
    }

    @Test
    void register_shouldThrowDuplicateKeyException_whenEmailAlreadyExists() {
        // Given
        when(sellerRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> sellerService.register(registerRequest))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("Seller already exists with this email");

        verify(sellerRepository).existsByEmail("john@example.com");
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    void register_shouldEncryptPassword() {
        // Given
        when(sellerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> {
            Seller seller = invocation.getArgument(0);
            seller.setId(1L);
            seller.setCreatedAt(Instant.now());
            return seller;
        });

        // When
        sellerService.register(registerRequest);

        // Then
        ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);
        verify(sellerRepository).save(sellerCaptor.capture());

        Seller savedSeller = sellerCaptor.getValue();
        assertThat(savedSeller.getPassword()).isNotEqualTo("password123");
        assertTrue(passwordEncoder.matches("password123", savedSeller.getPassword()));
    }

    @Test
    void register_shouldHandleDifferentAccountTypes() {
        // Given
        registerRequest.setAccountType(Role.INDİVİDUAL);

        when(sellerRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(sellerRepository.save(any(Seller.class))).thenAnswer(invocation -> {
            Seller seller = invocation.getArgument(0);
            seller.setId(1L);
            seller.setCreatedAt(Instant.now());
            return seller;
        });

        // When
        sellerService.register(registerRequest);

        // Then
        ArgumentCaptor<Seller> sellerCaptor = ArgumentCaptor.forClass(Seller.class);
        verify(sellerRepository).save(sellerCaptor.capture());

        Seller savedSeller = sellerCaptor.getValue();
        assertThat(savedSeller.getAccountType()).isEqualTo(Role.INDİVİDUAL);
    }

    @Test
    void get_shouldReturnSeller_whenSellerExists() {
        // Given
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));

        // When
        Seller result = sellerService.get(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        assertThat(result.getAccountType()).isEqualTo(Role.COMPANY);

        verify(sellerRepository).findById(1L);
    }

    @Test
    void get_shouldThrowNoSuchElementException_whenSellerDoesNotExist() {
        // Given
        when(sellerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sellerService.get(999L))
                .isInstanceOf(NoSuchElementException.class);

        verify(sellerRepository).findById(999L);
    }

    @Test
    void login_shouldReturnSpringUser_whenCredentialsAreValid() {
        // Given
        when(sellerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testSeller));

        // When
        User user = sellerService.login(loginRequest);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo(testSeller.getPassword());
        assertThat(user.getAuthorities()).hasSize(1);
        assertThat(user.getAuthorities()).contains(new SimpleGrantedAuthority("COMPANY"));

        verify(sellerRepository).findByEmail("john@example.com");
    }

    @Test
    void login_shouldThrowRuntimeException_whenSellerNotFound() {
        // Given
        when(sellerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        loginRequest.setEmail("nonexistent@example.com");

        // When & Then
        assertThatThrownBy(() -> sellerService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Seller not found");

        verify(sellerRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void login_shouldThrowRuntimeException_whenPasswordIsInvalid() {
        // Given
        when(sellerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testSeller));

        loginRequest.setPassword("wrongpassword");

        // When & Then
        assertThatThrownBy(() -> sellerService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid password");

        verify(sellerRepository).findByEmail("john@example.com");
    }

    @Test
    void login_shouldHandleDifferentAccountTypes() {
        // Given - Test with INDİVİDUAL account type
        Seller individualSeller = Seller.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .password(passwordEncoder.encode("password123"))
                .accountType(Role.INDİVİDUAL)
                .createdAt(Instant.now())
                .build();

        loginRequest.setEmail("jane@example.com");

        when(sellerRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(individualSeller));

        // When
        User user = sellerService.login(loginRequest);

        // Then
        assertThat(user.getAuthorities()).contains(new SimpleGrantedAuthority("INDİVİDUAL"));
    }

    @Test
    void register_shouldHandleCaseInsensitiveEmailCheck() {
        // Given
        registerRequest.setSellerEmail("JOHN@EXAMPLE.COM");

        when(sellerRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> sellerService.register(registerRequest))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("Seller already exists with this email");

        verify(sellerRepository).existsByEmail("john@example.com");
    }

    @Test
    void login_shouldVerifyPasswordCorrectly() {
        // Given
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Seller seller = Seller.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password(encodedPassword)
                .accountType(Role.COMPANY)
                .createdAt(Instant.now())
                .build();

        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword(rawPassword);

        when(sellerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(seller));

        // When
        User user = sellerService.login(loginRequest);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("test@example.com");
        assertTrue(passwordEncoder.matches(rawPassword, user.getPassword()));
    }
}

