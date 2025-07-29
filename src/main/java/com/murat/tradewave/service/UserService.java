package com.murat.tradewave.service;

import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.dto.user.request.UserRequest;
import com.murat.tradewave.dto.user.response.UserResponse;
import org.springframework.security.core.userdetails.User;


public interface UserService {

    UserResponse registerUser(UserRequest userRequest);
    User login(UserLogRequest userResponse);

}
