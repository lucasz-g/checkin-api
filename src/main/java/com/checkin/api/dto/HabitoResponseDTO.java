package com.checkin.api.dto;

import java.time.LocalDateTime;

/**
 * DTO de saída para representação de hábitos de saúde. Inclui
 * identificador, nome, descrição, meta e data de criação.
 */
public record HabitoResponseDTO(
        Long id,
        String nome,
        String descricao,
        String meta,
        LocalDateTime dataCriacao
) {
}
