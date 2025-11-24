package com.murat.tradewave.Service;

import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.dto.user.request.UserRequest;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.model.User;
import com.murat.tradewave.repository.UserRepository;
import com.murat.tradewave.security.JwtService;
import com.murat.tradewave.service.UserImplService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserImplServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserImplService userService;

    @Test
    void login_withValidCredentials_returnsUserResponse() {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded")
                .name("Test User")
                .role(Role.USER)
                .build();
        UserLogRequest request = new UserLogRequest();
        request.setEmail("test@example.com");
        request.setPassword("raw");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);
        when(jwtService.generateToken("test@example.com")).thenReturn("test-token");

        UserResponse result = userService.login(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals("test-token", result.getToken());
        verify(jwtService).generateToken("test@example.com");
    }

    @Test
    void login_whenUserNotFound_throwsException() {
        UserLogRequest request = new UserLogRequest();
        request.setEmail("missing@example.com");
        request.setPassword("pass");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.login(request));
        assertEquals("User does not exist", ex.getMessage());
    }

    @Test
    void login_withInvalidPassword_throwsException() {
        User user = User.builder()
                .email("test@example.com")
                .password("encoded")
                .role(Role.USER)
                .build();
        UserLogRequest request = new UserLogRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.login(request));
        assertEquals("Invalid password", ex.getMessage());
    }

    @Test
    void registerUser_whenNewEmail_returnsUserResponse() {
        UserRequest request = UserRequest.builder()
                .email("new@example.com")
                .password("raw")
                .name("New User")
                .build();
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("raw")).thenReturn("encoded");
        when(jwtService.generateToken("new@example.com")).thenReturn("token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse response = userService.registerUser(request);

        assertEquals(1L, response.getId());
        assertEquals("New User", response.getName());
        assertEquals("new@example.com", response.getEmail());
        assertEquals("token", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_whenEmailExists_throwsException() {
        UserRequest request = UserRequest.builder()
                .email("exist@example.com")
                .password("raw")
                .name("Test User")
                .build();
        when(userRepository.findByEmail("exist@example.com")).thenReturn(Optional.of(new User()));

        Exception ex = assertThrows(Exception.class, () -> userService.registerUser(request));
        assertTrue(ex.getMessage().contains("already exists") || ex.getMessage().contains("User"));
        verify(userRepository, never()).save(any());
    }

}
