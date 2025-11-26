package com.nttdata.user_service.application.ports;

import com.nttdata.user_service.application.dto.UserRequest;
import com.nttdata.user_service.application.dto.UserResponse;
import java.util.UUID;

public interface IUpdateUser {
    UserResponse execute(UUID id, UserRequest request, String rawToken);
}