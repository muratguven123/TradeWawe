package com.murat.tradewave.service;

import com.murat.tradewave.dto.user.request.UserLogRequest;
import com.murat.tradewave.dto.user.request.UserRequest;
import com.murat.tradewave.dto.user.response.UserResponse;


public interface UserService {

    UserResponse registerUser(UserRequest userRequest);
    UserResponse login(UserLogRequest userLogRequest);

}
