package com.nttdata.user_service.application.usecases;

import com.nttdata.user_service.application.dto.UserRequest;
import com.nttdata.user_service.application.dto.UserResponse;
import com.nttdata.user_service.application.ports.ICreateUser;
import com.nttdata.user_service.domain.exception.BusinessException;
import com.nttdata.user_service.domain.model.User;
import com.nttdata.user_service.domain.repository.UserRepository;
import com.nttdata.user_service.infrastructure.security.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCase implements ICreateUser {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.validation.password-regex:^.*$}")
    private String passwordRegex;

    private final String EMAIL_REGEX = "^[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,5}$";

    public CreateUserUseCase(UserRepository userRepository, JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse execute(UserRequest request) {
        if (!request.getEmail().matches(EMAIL_REGEX)) {
            throw new BusinessException("El formato del correo es incorrecto");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo ya registrado");
        }

        if (!request.getPassword().matches(passwordRegex)) {
            throw new BusinessException("La contraseña no cumple el formato requerido");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhones(request.getPhones());

        String token = jwtProvider.createToken(request.getEmail());
        user.setToken(token);

        User savedUser = userRepository.save(user);
        return UserResponse.fromEntity(savedUser);
    }
}