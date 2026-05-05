package com.checkin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para criação e atualização de hábitos de saúde. Contém
 * informações básicas como nome, descrição e meta diária. O usuário é
 * identificado no path ou em parametros do endpoint.
 */
public record HabitoRequestDTO(
        @NotBlank(message = "Nome do hábito é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String nome,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao,

        @Size(max = 100, message = "Meta deve ter no máximo 100 caracteres")
        String meta
) {
}
