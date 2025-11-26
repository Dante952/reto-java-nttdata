package com.nttdata.user_service.domain.exception;

public class BusinessException extends RuntimeException { // Mejor RuntimeException para transaction rollback
    public BusinessException(String message) {
        super(message);
    }
}