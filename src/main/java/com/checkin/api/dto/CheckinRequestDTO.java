package com.checkin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para receber dados de check-in do cliente.
 *
 * Contém validações para garantir integridade dos dados
 * e prevenir ataques de injeção.
 */
public record CheckinRequestDTO(
        @NotNull(message = "ID do usuário é obrigatório")
        Long usuarioId,

        @NotNull(message = "Status da tarefa é obrigatório")
        Boolean tarefaConcluida,

        @NotBlank(message = "Diário/motivo é obrigatório")
        @Size(min = 10, max = 1000, message = "Diário deve ter entre 10 e 1000 caracteres")
        String diario
) {
}
