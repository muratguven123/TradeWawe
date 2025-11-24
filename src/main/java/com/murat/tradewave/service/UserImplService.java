package com.murat.tradewave.service;

import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.dto.user.request.UserRequest;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.repository.UserRepository;
import com.murat.tradewave.security.JwtService;
import com.murat.tradewave.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserImplService implements UserService {
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse login(UserLogRequest userLogRequest) {
        User user = userRepository.findByEmail(userLogRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User does not exist"));

        if (!passwordEncoder.matches(userLogRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .build();
    }

    @Override
    @Transactional
    public UserResponse registerUser(UserRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("User already exists");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.USER)
                .build();
        User savedUser = userRepository.saveAndFlush(user);
        String token = jwtService.generateToken(savedUser.getEmail());
        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .token(token)
                .build();

    }
}