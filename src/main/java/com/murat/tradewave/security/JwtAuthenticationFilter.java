package com.murat.tradewave.security;

import com.murat.tradewave.repository.UserRepository;
import com.murat.tradewave.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component


public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String autheader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if(autheader==null||!autheader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        jwt = autheader.substring(7);
        userEmail=jwtService.extractUsername(jwt);
        if(userEmail!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
            User user=userRepository.findByEmail(userEmail).orElse(null);

            if(user!=null&& jwtService.isTokenValid(jwt,user.getEmail())){
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user,null,null);
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
            filterChain.doFilter(request, response);

        }
        filterChain.doFilter(request, response);
    }
}
