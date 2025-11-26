package com.nttdata.user_service.application.ports;

import com.nttdata.user_service.application.dto.UserRequest;
import com.nttdata.user_service.application.dto.UserResponse;

public interface ICreateUser {
    UserResponse execute(UserRequest request);
}