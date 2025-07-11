package com.murat.tradewave.service;

import com.murat.tradewave.dto.request.UserLogRequest;
import com.murat.tradewave.dto.request.UserRequest;
import com.murat.tradewave.dto.response.UserResponse;
import org.springframework.stereotype.Service;

@Service

public interface UserService {

    UserResponse registerUser(UserRequest userRequest);
    UserResponse login(UserLogRequest userLogRequest);

}
