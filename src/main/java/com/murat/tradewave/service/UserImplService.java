package com.murat.tradewave.service;

import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.dto.user.request.UserRequest;
import com.murat.tradewave.dto.user.response.UserResponse;
import com.murat.tradewave.Enums.Role;
import com.murat.tradewave.repository.UserRepository;
import com.murat.tradewave.security.JwtService;
import com.murat.tradewave.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserImplService implements UserService {
    @Override
    public UserResponse login(UserLogRequest userLogRequest) {
        return null;
    }

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();


    public UserResponse registerUser(UserRequest request){
        if (!(userRepository.existsByEmail(request.getEmail()))){
            throw new RuntimeException("Email address does not exist");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        String token = jwtService.generateJwtToken(user.getEmail());
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .build();

    }


}
