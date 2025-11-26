package com.nttdata.user_service.application.usecases;

import com.nttdata.user_service.application.dto.UserRequest;
import com.nttdata.user_service.application.dto.UserResponse;
import com.nttdata.user_service.application.ports.IUpdateUser;
import com.nttdata.user_service.domain.exception.BusinessException;
import com.nttdata.user_service.domain.model.User;
import com.nttdata.user_service.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UpdateUserUseCase implements IUpdateUser {

    private final UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse execute(UUID id, UserRequest request, String rawToken) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        String tokenLimpio = rawToken != null ? rawToken.replace("Bearer ", "") : "";

        if (!user.validateTokenOwnership(tokenLimpio)) {
            throw new BusinessException("Token inválido o expirado para este usuario");
        }

        user.setName(request.getName());
        user.setModified(LocalDateTime.now());

        return UserResponse.fromEntity(userRepository.save(user));
    }
}