package com.checkin.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO utilizado no endpoint de registro de novos usuários. Contém
 * informações essenciais para criação de um usuário: nome, email e senha.
 */
public record RegisterRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 120, message = "Senha deve ter entre 6 e 120 caracteres")
        String senha
) {
}
