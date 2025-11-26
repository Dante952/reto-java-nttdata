package com.nttdata.user_service.infrastructure.entrypoint.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.user_service.application.dto.UserRequest;
import com.nttdata.user_service.application.dto.UserResponse;
import com.nttdata.user_service.application.ports.ICreateUser;
import com.nttdata.user_service.application.ports.IUpdateUser;
import com.nttdata.user_service.domain.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ICreateUser createUserUseCase;

    @MockitoBean
    private IUpdateUser updateUserUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        // Arrange
        UserRequest request = new UserRequest();
        request.setName("Test User");
        request.setEmail("test@test.cl");
        request.setPassword("pass123");

        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .token("token-xyz")
                .isActive(true)
                .build();

        when(createUserUseCase.execute(any(UserRequest.class))).thenReturn(response);


        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.token").value("token-xyz"));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenBusinessExceptionOccurs() throws Exception {

        UserRequest request = new UserRequest();
        when(createUserUseCase.execute(any())).thenThrow(new BusinessException("Error de negocio"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error de negocio"));
    }

    @Test
    void updateUser_ShouldReturnOk_WhenRequestIsValid() throws Exception {

        UUID id = UUID.randomUUID();
        UserRequest request = new UserRequest();
        request.setName("Updated Name");
        String token = "Bearer token123";

        UserResponse response = UserResponse.builder()
                .id(id)
                .build();

        when(updateUserUseCase.execute(eq(id), any(UserRequest.class), eq(token)))
                .thenReturn(response);


        mockMvc.perform(put("/api/users/{id}", id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }
}