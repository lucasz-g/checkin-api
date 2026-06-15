package com.checkin.api.service;

import com.checkin.api.dto.HabitoRequestDTO;
import com.checkin.api.dto.HabitoResponseDTO;
import com.checkin.api.exception.ResourceNotFoundException;
import com.checkin.api.model.Habito;
import com.checkin.api.model.Usuario;
import com.checkin.api.repository.HabitoRepository;
import com.checkin.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio relacionada aos hábitos de saúde.
 * Permite criar, listar e remover hábitos associados a um usuário.
 */
@Service
public class HabitoService implements HabitoServicePort {
    private final HabitoRepository habitoRepository;
    private final UsuarioRepository usuarioRepository;

    public HabitoService(HabitoRepository habitoRepository, UsuarioRepository usuarioRepository) {
        this.habitoRepository = habitoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Cria um novo hábito para um determinado usuário.
     *
     * @param usuarioId  identificador do usuário
     * @param requestDTO dados do hábito
     * @return DTO de resposta contendo os dados do hábito criado
     */
    @Transactional
    @Override
    public HabitoResponseDTO criarHabito(Long usuarioId, HabitoRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + usuarioId + " não encontrado"));
        Habito habito = new Habito();
        habito.setNome(requestDTO.nome());
        habito.setDescricao(requestDTO.descricao());
        habito.setMeta(requestDTO.meta());
        habito.setUsuario(usuario);
        Habito salvo = habitoRepository.save(habito);
        return mapToDTO(salvo);
    }

    /**
     * Lista todos os hábitos de um usuário.
     *
     * @param usuarioId identificador do usuário
     * @return lista de DTOs com os hábitos
     */
    @Transactional(readOnly = true)
    @Override
    public List<HabitoResponseDTO> listarHabitosPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário com ID " + usuarioId + " não encontrado");
        }
        return habitoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Remove um hábito pelo seu ID. Caso o hábito não exista, lança exceção.
     *
     * @param id identificador do hábito
     */
    @Transactional
    @Override
    public void deletarHabito(Long id) {
        Habito habito = habitoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hábito com ID " + id + " não encontrado"));
        habitoRepository.delete(habito);
    }

    private HabitoResponseDTO mapToDTO(Habito habito) {
        return new HabitoResponseDTO(
                habito.getId(),
                habito.getNome(),
                habito.getDescricao(),
                habito.getMeta(),
                habito.getDataCriacao()
        );
    }
}
