package com.murat.tradewave.service;

import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.model.Product;
import com.murat.tradewave.model.User;
import com.murat.tradewave.repository.ProductionRepository;
import com.murat.tradewave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ProductionRepository productRepository;
    private final Mapper mapper;

    @Override
    @Transactional
    public void deleteUser(Long id) {
        // Validation
        if (id == null || id <= 0) {
            log.warn("Admin attempted to delete user with invalid ID: {}", id);
            throw new IllegalArgumentException("Invalid user ID");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Admin attempted to delete non-existent user with ID: {}", id);
                    return new NoSuchElementException("User not found with ID: " + id);
                });

        // Prevent deleting admin users (optional security measure)
        if (user.getRole() == Role.ADMIN) {
            log.warn("Attempted to delete admin user. ID: {}, Email: {}", id, user.getEmail());
            throw new IllegalStateException("Cannot delete admin users");
        }

        userRepository.delete(user);
        log.info("Admin deleted user successfully. ID: {}, Email: {}", id, user.getEmail());
    }

    @Override
    @Transactional
    public UserResponse changeUserRole(Long id, Role newRole) {
        // Validation
        if (id == null || id <= 0) {
            log.warn("Admin attempted to change role with invalid user ID: {}", id);
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (newRole == null) {
            log.warn("Admin attempted to change role with null role for user ID: {}", id);
            throw new IllegalArgumentException("Role cannot be null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Admin attempted to change role for non-existent user ID: {}", id);
                    return new NoSuchElementException("User not found with ID: " + id);
                });

        Role oldRole = user.getRole();

        // Prevent changing role of the last admin (optional security measure)
        if (oldRole == Role.ADMIN && newRole != Role.ADMIN) {
            long adminCount = userRepository.countUserByRole(Role.ADMIN).ordinal();
            if (adminCount <= 1) {
                log.warn("Attempted to remove last admin role. User ID: {}", id);
                throw new IllegalStateException("Cannot remove the last admin user");
            }
        }

        user.setRole(newRole);
        userRepository.save(user);

        log.info("Admin changed user role successfully. User ID: {}, Email: {}, Old Role: {}, New Role: {}",
                id, user.getEmail(), oldRole, newRole);

        return mapper.mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.debug("Admin fetching all users");
        List<User> users = userRepository.findAll();
        log.info("Admin retrieved {} users", users.size());
        return users;
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        // Validation
        if (productId == null || productId <= 0) {
            log.warn("Admin attempted to delete product with invalid ID: {}", productId);
            throw new IllegalArgumentException("Invalid product ID");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Admin attempted to delete non-existent product with ID: {}", productId);
                    return new NoSuchElementException("Product not found with ID: " + productId);
                });

        productRepository.delete(product);
        log.info("Admin deleted product successfully. Product ID: {}, Name: {}", productId, product.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        // Validation
        if (id == null || id <= 0) {
            log.warn("Admin attempted to get user with invalid ID: {}", id);
            throw new IllegalArgumentException("Invalid user ID");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Admin attempted to get non-existent user with ID: {}", id);
                    return new NoSuchElementException("User not found with ID: " + id);
                });

        log.debug("Admin retrieved user. ID: {}, Email: {}", id, user.getEmail());
        return user;
    }

    @Override
    @Transactional
    public UserResponse toggleUserStatus(Long id, boolean active) {
        // Validation
        if (id == null || id <= 0) {
            log.warn("Admin attempted to toggle user status with invalid ID: {}", id);
            throw new IllegalArgumentException("Invalid user ID");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Admin attempted to toggle status for non-existent user ID: {}", id);
                    return new NoSuchElementException("User not found with ID: " + id);
                });

        // Prevent deactivating admin users
        if (!active && user.getRole() == Role.ADMIN) {
            log.warn("Attempted to deactivate admin user. ID: {}, Email: {}", id, user.getEmail());
            throw new IllegalStateException("Cannot deactivate admin users");
        }

        // Note: This assumes User model has an 'active' field.
        // If not, you can add it or remove this method.
        // user.setActive(active);
        userRepository.save(user);

        log.info("Admin toggled user status. User ID: {}, Email: {}, Active: {}",
                id, user.getEmail(), active);

        return mapper.mapToUserResponse(user);
    }
}

