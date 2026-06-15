package com.checkin.api.service;

import com.checkin.api.dto.CheckinRequestDTO;
import com.checkin.api.dto.CheckinResponseDTO;
import com.checkin.api.dto.HistoricoResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckinServicePort {

    CheckinResponseDTO criarCheckin(CheckinRequestDTO requestDTO);

    List<HistoricoResponseDTO> buscarHistorico(Long usuarioId);

    List<HistoricoResponseDTO> buscarHistoricoPorPeriodo(
            Long usuarioId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    );
}
