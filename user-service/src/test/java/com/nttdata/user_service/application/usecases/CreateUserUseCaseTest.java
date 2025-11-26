package com.nttdata.user_service.application.usecases;

import com.nttdata.user_service.application.dto.UserRequest;
import com.nttdata.user_service.application.dto.UserResponse;
import com.nttdata.user_service.domain.exception.BusinessException;
import com.nttdata.user_service.domain.model.Phone;
import com.nttdata.user_service.domain.model.User;
import com.nttdata.user_service.domain.repository.UserRepository;
import com.nttdata.user_service.infrastructure.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private UserRequest validRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(createUserUseCase, "passwordRegex", "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

        validRequest = new UserRequest();
        validRequest.setName("Juan Rodriguez");
        validRequest.setEmail("juan@rodriguez.org");
        validRequest.setPassword("hunter22");

        Phone phone = new Phone();
        phone.setNumber("1234567");
        phone.setCityCode("1");
        phone.setCountryCode("57");
        validRequest.setPhones(List.of(phone));
    }

    @Test
    void execute_ShouldCreateUser_WhenDataIsValid() {
        // Arrange
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtProvider.createToken(anyString())).thenReturn("mockToken");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail(validRequest.getEmail());
        savedUser.setToken("mockToken");
        savedUser.setActive(true);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);


        UserResponse response = createUserUseCase.execute(validRequest);


        assertNotNull(response);
        assertEquals(savedUser.getId(), response.getId());
        assertEquals("mockToken", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void execute_ShouldThrowException_WhenEmailIsInvalid() {

        validRequest.setEmail("correo-invalido");


        BusinessException exception = assertThrows(BusinessException.class, () -> {
            createUserUseCase.execute(validRequest);
        });
        assertEquals("El formato del correo es incorrecto", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowException_WhenEmailAlreadyExists() {

        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            createUserUseCase.execute(validRequest);
        });
        assertEquals("El correo ya registrado", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_ShouldThrowException_WhenPasswordIsWeak() {

        validRequest.setPassword("123");
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            createUserUseCase.execute(validRequest);
        });
        assertEquals("La contraseña no cumple el formato requerido", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}