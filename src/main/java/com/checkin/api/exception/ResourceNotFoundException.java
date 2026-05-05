package com.checkin.api.exception;

/**
 * Exceção lançada quando um recurso não é encontrado.
 * 
 * Utilizada para indicar que uma entidade buscada não existe no banco de dados.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
