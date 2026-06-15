package com.checkin.api.service;

import com.checkin.api.dto.HabitoRequestDTO;
import com.checkin.api.dto.HabitoResponseDTO;

import java.util.List;

public interface HabitoServicePort {

    HabitoResponseDTO criarHabito(Long usuarioId, HabitoRequestDTO requestDTO);

    List<HabitoResponseDTO> listarHabitosPorUsuario(Long usuarioId);

    void deletarHabito(Long id);
}
