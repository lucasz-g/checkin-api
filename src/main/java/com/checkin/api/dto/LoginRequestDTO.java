package com.checkin.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado no endpoint de login. Contém email e senha do usuário.
 */
public record LoginRequestDTO(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha
) {
}
