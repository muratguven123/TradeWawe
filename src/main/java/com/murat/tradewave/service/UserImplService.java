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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserImplService implements UserService {
    private final Mapper mapper;

    @Override
    public org.springframework.security.core.userdetails.User login(UserLogRequest UserLogRequest) {
        User user = userRepository.findByEmail(UserLogRequest.getEmail()).orElseThrow(()->new RuntimeException("User does not exist"));
        return new org.springframework.security.core.userdetails.User(
                UserLogRequest.getEmail(),
                UserLogRequest.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
        );
    }

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();


    public UserResponse registerUser(UserRequest request){
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
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public void deleteUserbyid(Long id){
        User deletedUser = userRepository.findById(id).orElseThrow(()->new RuntimeException("UserNotFound"));
        userRepository.delete(deletedUser);
    }
    public UserResponse changeRole(Long id,Role newRole){
        User changedRole = userRepository.findById(id).orElseThrow(()->new RuntimeException("user not found"));
        changedRole.setRole(newRole);
        return mapper.mapToUserResponse(changedRole);
    }

}
