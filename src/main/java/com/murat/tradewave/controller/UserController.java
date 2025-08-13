package com.murat.tradewave.controller;

import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.dto.user.request.UserRequest;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.service.UserImplService;
import com.murat.tradewave.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor

public class UserController {

private final UserImplService userService;

@PostMapping("/register")
    public UserResponse register(@RequestBody @Valid UserRequest userRequest) {
       return userService.registerUser(userRequest);
}
@PostMapping("/login")
    public User login(@RequestBody @Valid UserLogRequest userLogRequest) {
        return userService.login(userLogRequest);
}

}
