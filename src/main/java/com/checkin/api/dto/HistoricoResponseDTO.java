package com.checkin.api.dto;

import java.time.LocalDateTime;

/**
 * DTO para retornar dados de histórico ao cliente.
 *
 * Estrutura otimizada para geração de gráficos de evolução.
 */
public record HistoricoResponseDTO(
        LocalDateTime data,
        Boolean tarefaConcluida,
        String diario
) {
}
