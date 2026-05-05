package com.checkin.api.dto;

import com.checkin.api.model.Checkin;

import java.time.LocalDateTime;

/**
 * DTO para retornar dados de check-in ao cliente.
 *
 * Evita exposição desnecessária de dados internos da entidade.
 */
public record CheckinResponseDTO(
        Long id,
        Long usuarioId,
        String usuarioNome,
        Boolean tarefaConcluida,
        String diario,
        LocalDateTime dataCheckin
) {
    public CheckinResponseDTO(Checkin checkin) {
        this(
                checkin.getId(),
                checkin.getUsuario().getId(),
                checkin.getUsuario().getNome(),
                checkin.getTarefaConcluida(),
                checkin.getDiario(),
                checkin.getDataCheckin()
        );
    }
}
