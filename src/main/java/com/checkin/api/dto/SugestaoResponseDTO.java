package com.checkin.api.dto;

/**
 * DTO para encapsular a resposta de uma sugestão de hábito obtida via API
 * externa. Atualmente contém apenas a descrição da atividade sugerida, mas
 * pode ser expandido para incluir outros campos retornados pela API.
 */
public record SugestaoResponseDTO(String sugestao) {
}
