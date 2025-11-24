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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    public org.springframework.security.core.userdetails.User login(UserLogRequest userLogRequest) {
        User user = userRepository.findByEmail(userLogRequest.getEmail()).orElseThrow(() -> new RuntimeException("User does not exist"));
        String requestedPassword = userLogRequest.getPassword();
        if (!passwordEncoder.matches(requestedPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return new org.springframework.security.core.userdetails.User(
                userLogRequest.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
        );
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

    @Transactional(readOnly = true)
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUserbyid(Long id){
        User deletedUser = userRepository.findById(id).orElseThrow(()->new RuntimeException("UserNotFound"));
        userRepository.delete(deletedUser);
    }

    @Transactional
    public UserResponse changeRole(Long id,Role newRole){
        User changedRole = userRepository.findById(id).orElseThrow(()->new RuntimeException("user not found"));
        changedRole.setRole(newRole);
        userRepository.save(changedRole);
        return mapper.mapToUserResponse(changedRole);
    }

}
