package com.murat.tradewave.service;

import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.model.User;

import java.util.List;

public interface AdminService {

    /**
     * Kullanıcıyı ID'ye göre siler
     * @param id Silinecek kullanıcının ID'si
     */
    void deleteUser(Long id);

    /**
     * Kullanıcının rolünü değiştirir
     * @param id Kullanıcı ID'si
     * @param newRole Yeni rol
     * @return Güncellenen kullanıcı bilgileri
     */
    UserResponse changeUserRole(Long id, Role newRole);

    /**
     * Tüm kullanıcıları listeler
     * @return Kullanıcı listesi
     */
    List<User> getAllUsers();

    /**
     * Herhangi bir ürünü siler (admin yetkisi)
     * @param productId Silinecek ürün ID'si
     */
    void deleteProduct(Long productId);

    /**
     * Belirli bir kullanıcıyı ID'ye göre getirir
     * @param id Kullanıcı ID'si
     * @return Kullanıcı bilgileri
     */
    User getUserById(Long id);

    /**
     * Kullanıcıyı aktif/pasif yapar
     * @param id Kullanıcı ID'si
     * @param active Aktif durumu
     * @return Güncellenen kullanıcı bilgileri
     */
    UserResponse toggleUserStatus(Long id, boolean active);
}

