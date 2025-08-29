package com.murat.tradewave.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.controller.AdminController;
import com.murat.tradewave.model.User;
import com.murat.tradewave.security.JwtAuthenticationFilter;
import com.murat.tradewave.service.ProductServiceImpl;
import com.murat.tradewave.service.UserImplService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class)
// DİKKAT: @AutoConfigureMockMvc(addFilters = false) YOK!
@Import(AdminControllerTest.SecurityTestConfig.class)
class AdminControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean UserImplService userService;
    @MockBean ProductServiceImpl productService;

    // Uygulamadaki gerçek bean’ı yerine sahte filtre; zincire eklemeyeceğiz
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired ObjectMapper objectMapper;

    @TestConfiguration
    @EnableMethodSecurity // @PreAuthorize'ları aktif et
    static class SecurityTestConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable());
            // Her istek kimlik doğrulanmış olsun; 403'ü method security verecek
            http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
            http.httpBasic(withDefaults());
            // ÖNEMLİ: jwtAuthenticationFilter'ı BURAYA EKLEME!
            return http.build();
        }

        @Bean
        UserDetailsService uds() {
            return new InMemoryUserDetailsManager(
                    org.springframework.security.core.userdetails.User
                            .withUsername("placeholder")
                            .password("{noop}pw")
                            .roles("USER")
                            .build()
            );
        }
    }

    @Test
    @DisplayName("GET /admin/users - USER -> 403")
    @WithMockUser(username = "user", roles = "USER")
    void getAllUsers_asNonAdmin_forbidden() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
        Mockito.verifyNoInteractions(userService, productService);
    }

    @Test
    @DisplayName("GET /admin/users - ADMIN -> 200 ve JSON")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getAllUsers_asAdmin_ok() throws Exception {
        var u1 = User.builder().id(1L).email("a@example.com").name("A").build();
        var u2 = User.builder().id(2L).email("b@example.com").name("B").build();
        when(userService.getAllUsers()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/admin/users").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("a@example.com"))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("b@example.com"))
                .andExpect(jsonPath("$[1].name").value("B"));

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("DELETE /admin/users/{id} - ADMIN -> 200")
    @WithMockUser(roles = "ADMIN")
    void deleteUser_asAdmin_ok() throws Exception {
        long id = 1L;
        doNothing().when(userService).deleteUserbyid(id);

        mockMvc.perform(delete("/admin/users/{id}", id))
                .andExpect(status().isOk());

        verify(userService).deleteUserbyid(id);
    }

    @Test
    @DisplayName("DELETE /admin/users/{id} - USER -> 403")
    @WithMockUser(roles = "USER")
    void deleteUser_asNonAdmin_forbidden() throws Exception {
        mockMvc.perform(delete("/admin/users/{id}", 99L))
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(userService, productService);
    }

    @Test
    @DisplayName("PUT /admin/users/{id}/role - ADMIN -> 200")
    @WithMockUser(roles = "ADMIN")
    void changeUserRole_asAdmin_ok() throws Exception {
        long id = 10L;
        Role newRole = Role.ADMIN;
        doReturn(null).when(userService).changeRole(id, newRole);

        mockMvc.perform(put("/admin/users/{id}/role", id)
                        .param("newRole", newRole.name()))
                .andExpect(status().isOk());

        verify(userService).changeRole(eq(id), eq(newRole));
    }

    @Test
    @DisplayName("PUT /admin/users/{id}/role - USER -> 403")
    @WithMockUser(roles = "USER")
    void changeUserRole_asNonAdmin_forbidden() throws Exception {
        mockMvc.perform(put("/admin/users/{id}/role", 1L)
                        .param("newRole", Role.ADMIN.name()))
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(userService, productService);
    }

    @Test
    @DisplayName("DELETE /admin/products/{id} - ADMIN -> 200")
    @WithMockUser(roles = "ADMIN")
    void deleteAnyProduct_asAdmin_ok() throws Exception {
        long id = 5L;
        doNothing().when(productService).deleteProduct(id);

        mockMvc.perform(delete("/admin/products/{id}", id))
                .andExpect(status().isOk());

        verify(productService).deleteProduct(id);
    }

    @Test
    @DisplayName("DELETE /admin/products/{id} - USER -> 403")
    @WithMockUser(roles = "USER")
    void deleteAnyProduct_asNonAdmin_forbidden() throws Exception {
        mockMvc.perform(delete("/admin/products/{id}", 11L))
                .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(userService, productService);
    }
}
