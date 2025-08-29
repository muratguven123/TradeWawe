package com.murat.tradewave.service;

import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.dto.user.request.UserRequest;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.model.User;
import com.murat.tradewave.repository.UserRepository;
import com.murat.tradewave.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserImplServiceTest {

    @Mock
    private Mapper mapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserImplService userService;

    @Test
    void login_withValidCredentials_returnsUserDetails() {
        User user = User.builder()
                .email("test@example.com")
                .password("encoded")
                .role(Role.USER)
                .build();
        UserLogRequest request = new UserLogRequest();
        request.setEmail("test@example.com");
        request.setPassword("raw");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw", "encoded")).thenReturn(true);

        org.springframework.security.core.userdetails.User result = userService.login(request);

        assertEquals("test@example.com", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
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
                .build();
        when(userRepository.findByEmail("exist@example.com")).thenReturn(Optional.of(new User()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.registerUser(request));
        assertEquals("User already exists", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_returnsUsersFromRepository() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
    }

    @Test
    void deleteUserbyid_whenUserExists_deletesUser() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserbyid(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserbyid_whenUserNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUserbyid(1L));
        assertEquals("UserNotFound", ex.getMessage());
        verify(userRepository, never()).delete(any());
    }

    @Test
    void changeRole_whenUserExists_updatesRole() {
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .name("User")
                .role(Role.USER)
                .build();
        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("user@example.com")
                .name("User")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.mapToUserResponse(user)).thenReturn(response);

        UserResponse result = userService.changeRole(1L, Role.ADMIN);

        assertEquals(Role.ADMIN, user.getRole());
        assertEquals(response, result);
        verify(userRepository).save(user);
    }

    @Test
    void changeRole_whenUserNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.changeRole(1L, Role.ADMIN));
        assertEquals("user not found", ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(mapper, never()).mapToUserResponse(any());
    }
}
