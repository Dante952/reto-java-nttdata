package com.nttdata.user_service.application.usecases;

import com.nttdata.user_service.application.dto.UserRequest;
import com.nttdata.user_service.application.dto.UserResponse;
import com.nttdata.user_service.domain.exception.BusinessException;
import com.nttdata.user_service.domain.model.User;
import com.nttdata.user_service.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    @Test
    void execute_ShouldUpdateUser_WhenTokenIsCorrect() {
        UUID userId = UUID.randomUUID();
        String rawToken = "Bearer tokenValido";
        String tokenLimpio = "tokenValido";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setToken(tokenLimpio);
        existingUser.setName("Old Name");

        UserRequest request = new UserRequest();
        request.setName("New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = updateUserUseCase.execute(userId, request, rawToken);

        assertNotNull(response);
        assertEquals(userId, response.getId());
        verify(userRepository).save(existingUser);
        assertEquals("New Name", existingUser.getName());
    }

    @Test
    void execute_ShouldThrowException_WhenUserNotFound() {

        UUID userId = UUID.randomUUID();
        UserRequest request = new UserRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                updateUserUseCase.execute(userId, request, "token")
        );
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void execute_ShouldThrowException_WhenTokenDoesNotMatch() {
        UUID userId = UUID.randomUUID();
        String rawToken = "Bearer tokenFalso";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setToken("tokenVerdadero");

        UserRequest request = new UserRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                updateUserUseCase.execute(userId, request, rawToken)
        );
        assertEquals("Token inválido o expirado para este usuario", ex.getMessage());
    }
}