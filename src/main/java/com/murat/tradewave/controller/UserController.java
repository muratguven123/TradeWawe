package com.murat.tradewave.controller;

import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.dto.user.request.UserRequest;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.service.UserImplService;
import com.murat.tradewave.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "Kullanıcı kayıt ve giriş işlemleri")
public class UserController {

private final UserImplService userService;

@PostMapping("/register")
@Operation(summary = "Kullanıcı Kayıt", description = "Yeni kullanıcı kaydı oluşturur")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Kullanıcı başarıyla kaydedildi"),
    @ApiResponse(responseCode = "400", description = "Geçersiz kullanıcı bilgileri"),
    @ApiResponse(responseCode = "409", description = "Kullanıcı zaten mevcut")
})
    public UserResponse register(@RequestBody @Valid UserRequest userRequest) {
       return userService.registerUser(userRequest);
}

@PostMapping("/login")
@Operation(summary = "Kullanıcı Giriş", description = "Kullanıcı giriş yapar ve token alır")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Başarılı giriş"),
    @ApiResponse(responseCode = "401", description = "Geçersiz kullanıcı bilgileri")
})
    public User login(@RequestBody @Valid UserLogRequest userLogRequest) {
        return userService.login(userLogRequest);
}

}
