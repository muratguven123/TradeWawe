package com.murat.tradewave.Controller;

import com.murat.tradewave.controller.UserController;
import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.dto.user.request.UserRequest;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.service.UserImplService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserImplService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @Test
    void registerDelegatesToService() {
        UserRequest request = new UserRequest();
        UserResponse response = new UserResponse();
        when(userService.registerUser(request)).thenReturn(response);
        UserResponse result = userController.register(request);
        verify(userService).registerUser(request);
        assertEquals(response, result);
    }
    @Test
    void loginDelegatesToService() {
        UserLogRequest request = new UserLogRequest();
        User user = new User("name", "pass", List.of());
        when(userService.login(request)).thenReturn(user);

        User result = userController.login(request);

        verify(userService).login(request);
        assertEquals(user, result);
    }
}