package com.murat.tradewave.controller;

import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.model.User;
import com.murat.tradewave.service.ProductServiceImpl;
import com.murat.tradewave.service.UserImplService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserImplService userService;
    private final ProductServiceImpl productService;

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserbyid(id);
    }

    @PutMapping("/users/{id}/role")
    public void changeUserRole(@PathVariable Long id, @RequestParam Role newRole) {
        userService.changeRole(id, newRole);
    }

    @DeleteMapping("/products/{id}")
    public void deleteAnyProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
