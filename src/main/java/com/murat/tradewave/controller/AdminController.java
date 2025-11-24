package com.murat.tradewave.controller;

import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.model.User;
import com.murat.tradewave.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Admin işlemleri - Kullanıcı ve ürün yönetimi")
public class AdminController {

    private final AdminService adminService;

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Kullanıcı Sil", description = "Belirtilen ID'ye sahip kullanıcıyı siler")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Kullanıcı başarıyla silindi"),
        @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
        @ApiResponse(responseCode = "403", description = "Yetkisiz erişim")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Kullanıcı Rolünü Değiştir", description = "Kullanıcının rolünü değiştirir")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol başarıyla değiştirildi"),
        @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
        @ApiResponse(responseCode = "403", description = "Yetkisiz erişim")
    })
    public ResponseEntity<UserResponse> changeUserRole(@PathVariable Long id, @RequestParam Role newRole) {
        UserResponse response = adminService.changeUserRole(id, newRole);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Ürün Sil", description = "Herhangi bir ürünü siler (admin yetkisi)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Ürün başarıyla silindi"),
        @ApiResponse(responseCode = "404", description = "Ürün bulunamadı"),
        @ApiResponse(responseCode = "403", description = "Yetkisiz erişim")
    })
    public ResponseEntity<Void> deleteAnyProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    @Operation(summary = "Tüm Kullanıcıları Listele", description = "Sistemdeki tüm kullanıcıları getirir")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Kullanıcılar başarıyla getirildi"),
        @ApiResponse(responseCode = "403", description = "Yetkisiz erişim")
    })
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Kullanıcı Detayı", description = "Belirtilen ID'ye sahip kullanıcının detaylarını getirir")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Kullanıcı bilgileri getirildi"),
        @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
        @ApiResponse(responseCode = "403", description = "Yetkisiz erişim")
    })
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Kullanıcı Durumunu Değiştir", description = "Kullanıcıyı aktif/pasif yapar")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Kullanıcı durumu değiştirildi"),
        @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı"),
        @ApiResponse(responseCode = "403", description = "Yetkisiz erişim")
    })
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        UserResponse response = adminService.toggleUserStatus(id, active);
        return ResponseEntity.ok(response);
    }
}
