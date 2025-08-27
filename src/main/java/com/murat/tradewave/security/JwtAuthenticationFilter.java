package com.murat.tradewave.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.Paths.get;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    @Qualifier("userDetailsServiceImpl")
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        final String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (ExpiredJwtException ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_EXPIRED", "JWT expired");
            SecurityContextHolder.clearContext();
            return;
        } catch (JwtException | IllegalArgumentException ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_INVALID", "Invalid JWT");
            SecurityContextHolder.clearContext();
            return;
        } catch (Exception ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "AUTH_ERROR", "Could not parse token");
            SecurityContextHolder.clearContext();
            return;
        }
        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }
        final var userDetails = loadUserSafely(username, response);
        if (userDetails == null) {
            SecurityContextHolder.clearContext();
            return;
        }

        final boolean valid;
        try {

            valid = jwtService.isTokenValid(token, userDetails.getUsername());
        } catch (ExpiredJwtException ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_EXPIRED", "JWT expired");
            SecurityContextHolder.clearContext();
            return;
        } catch (JwtException | IllegalArgumentException ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_INVALID", "Invalid JWT");
            SecurityContextHolder.clearContext();
            return;
        } catch (Exception ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "AUTH_ERROR", "Token validation error");
            SecurityContextHolder.clearContext();
            return;
        }

        if (!valid) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_INVALID", "Invalid or mismatched JWT");
            SecurityContextHolder.clearContext();
            return;
        }
        List<SimpleGrantedAuthority> authorities;
        try {
            ;
            authorities = mapRolesToAuthorities(get("roles"));
        } catch (ExpiredJwtException ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_EXPIRED", "JWT expired");
            SecurityContextHolder.clearContext();
            return;
        } catch (JwtException | IllegalArgumentException ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "TOKEN_INVALID", "Invalid JWT");
            SecurityContextHolder.clearContext();
            return;
        } catch (Exception ex) {

            authorities = Collections.emptyList();
        }
        var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        chain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> mapRolesToAuthorities(Object rolesClaim) {
        List<String> roles;
        if (rolesClaim == null) {
            roles = List.of();
        } else if (rolesClaim instanceof List<?> list) {
            roles = list.stream().map(String::valueOf).collect(Collectors.toList());
        } else if (rolesClaim instanceof String s) {
            roles = Arrays.stream(s.split(","))
                    .map(String::trim)
                    .filter(str -> !str.isEmpty())
                    .collect(Collectors.toList());
        } else {
            roles = List.of(String.valueOf(rolesClaim));
        }

        return roles.stream()
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private org.springframework.security.core.userdetails.UserDetails loadUserSafely(
            String username, HttpServletResponse response) throws IOException {

        try {
            return userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "USER_NOT_FOUND", "User not found");
        } catch (BadCredentialsException ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "BAD_CREDENTIALS", "Bad credentials");
        } catch (Exception ex) {
            writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED, "AUTH_ERROR", "User lookup failed");
        }
        return null;
    }

    private void writeJsonError(HttpServletResponse response, int status, String code, String message) throws IOException {
        if (response.isCommitted()) return;
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String body = "{\"error\":\"" + escape(code) + "\",\"message\":\"" + escape(message) + "\"}";
        response.getWriter().write(body);
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

}
