package com.nttdata.user_service.infrastructure.entrypoint.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleGeneralException_ShouldReturnInternalServerError_WithErrorMessage() {
        String errorMessage = "Error inesperado de base de datos";
        Exception exception = new RuntimeException(errorMessage);

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGeneralException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        String responseMessage = response.getBody().get("mensaje");
        assertEquals("Error interno: " + errorMessage, responseMessage);
    }
}