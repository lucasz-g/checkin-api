package com.checkin.api.dto;

/**
 * DTO utilizado como resposta nos endpoints de autenticação, contendo o
 * token JWT gerado. Pode ser expandido para incluir outras informações
 * como data de expiração ou tipo de token.
 */
public record AuthResponseDTO(String token) {
}
