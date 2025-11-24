package com.murat.tradewave.config;

import com.murat.tradewave.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/error",
            "/address/**",
            "/cart/**",
            "/category/**",
            "/orders/**",
            "/products/**",
            "/sellers/**",
            "/user/**",
            "/seller/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var c = new CorsConfiguration();
                    c.setAllowedOrigins(List.of("*"));
                    c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    c.setAllowedHeaders(List.of("*"));
                    c.setAllowCredentials(false);
                    return c;
                }))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/payment/**").authenticated()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            if (!res.isCommitted()) {
                                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                res.setContentType("application/json");
                                res.setCharacterEncoding("UTF-8");
                                res.getWriter().write("{\"error\":\"UNAUTHORIZED\",\"message\":\"Authentication required or failed\"}");
                            }
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            if (!res.isCommitted()) {
                                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                res.setContentType("application/json");
                                res.setCharacterEncoding("UTF-8");
                                res.getWriter().write("{\"error\":\"FORBIDDEN\",\"message\":\"Access denied\"}");
                            }
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
