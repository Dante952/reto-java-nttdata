package com.nttdata.user_service.infrastructure.entrypoint.api;

import com.nttdata.user_service.application.dto.UserRequest;
import com.nttdata.user_service.application.dto.UserResponse;
import com.nttdata.user_service.application.ports.ICreateUser;
import com.nttdata.user_service.application.ports.IUpdateUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ICreateUser createUserUseCase;
    private final IUpdateUser updateUserUseCase;

    public UserController(ICreateUser createUserUseCase, IUpdateUser updateUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        UserResponse response = createUserUseCase.execute(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequest request,
            @RequestHeader(value = "Authorization", required = true) String token) {

        UserResponse response = updateUserUseCase.execute(id, request, token);
        return ResponseEntity.ok(response);
    }
}