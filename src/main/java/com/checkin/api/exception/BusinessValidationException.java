package com.checkin.api.exception;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 * 
 * Utilizada para validações específicas da lógica de negócio.
 */
public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
