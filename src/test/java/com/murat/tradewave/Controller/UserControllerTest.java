package com.murat.tradewave.Controller;

import com.murat.tradewave.controller.UserController;
import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.dto.user.request.UserRequest;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.service.UserImplService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserImplService userService;

    @InjectMocks
    private UserController userController;

    private UserRequest registerRequest;
    private UserLogRequest loginRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        // Setup register request
        registerRequest = UserRequest.builder()
                .email("test@example.com")
                .password("password123")
                .name("Test User")
                .build();

        // Setup login request
        loginRequest = new UserLogRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Setup user response
        userResponse = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .token("test-jwt-token")
                .build();
    }

    @Test
    void register_shouldReturnUserResponse_whenValidRequest() {
        // Given
        when(userService.registerUser(registerRequest)).thenReturn(userResponse);

        // When
        UserResponse result = userController.register(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals(userResponse, result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals("test-jwt-token", result.getToken());
        verify(userService).registerUser(registerRequest);
    }

    @Test
    void register_shouldCallServiceWithCorrectRequest() {
        // Given
        when(userService.registerUser(any(UserRequest.class))).thenReturn(userResponse);

        // When
        userController.register(registerRequest);

        // Then
        verify(userService).registerUser(registerRequest);
    }

    @Test
    void login_shouldReturnUserResponse_whenValidCredentials() {
        // Given
        when(userService.login(loginRequest)).thenReturn(userResponse);

        // When
        UserResponse result = userController.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals(userResponse, result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals("test-jwt-token", result.getToken());
        verify(userService).login(loginRequest);
    }

    @Test
    void login_shouldCallServiceWithCorrectRequest() {
        // Given
        when(userService.login(any(UserLogRequest.class))).thenReturn(userResponse);

        // When
        userController.login(loginRequest);

        // Then
        verify(userService).login(loginRequest);
    }

    @Test
    void register_shouldReturnTokenInResponse() {
        // Given
        when(userService.registerUser(registerRequest)).thenReturn(userResponse);

        // When
        UserResponse result = userController.register(registerRequest);

        // Then
        assertNotNull(result.getToken());
        assertThat(result.getToken()).isEqualTo("test-jwt-token");
    }

    @Test
    void login_shouldReturnTokenInResponse() {
        // Given
        when(userService.login(loginRequest)).thenReturn(userResponse);

        // When
        UserResponse result = userController.login(loginRequest);

        // Then
        assertNotNull(result.getToken());
        assertThat(result.getToken()).isEqualTo("test-jwt-token");
    }
}