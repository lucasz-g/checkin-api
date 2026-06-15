package com.checkin.api.service;

import com.checkin.api.dto.CheckinRequestDTO;
import com.checkin.api.dto.CheckinResponseDTO;
import com.checkin.api.dto.HistoricoResponseDTO;
import com.checkin.api.exception.BusinessValidationException;
import com.checkin.api.exception.ResourceNotFoundException;
import com.checkin.api.model.Checkin;
import com.checkin.api.model.Usuario;
import com.checkin.api.repository.CheckinRepository;
import com.checkin.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço responsável pela lógica de negócio de check-ins.
 * 
 * Camada de serviço que implementa validações, regras de negócio
 * e coordena operações entre repositories.
 */
@Service
public class CheckinService implements CheckinServicePort {

    private final CheckinRepository checkinRepository;
    private final UsuarioRepository usuarioRepository;

    public CheckinService(CheckinRepository checkinRepository, UsuarioRepository usuarioRepository) {
        this.checkinRepository = checkinRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Cria um novo check-in após validações.
     * 
     * Valida se o usuário existe e se os dados são válidos antes de persistir.
     * 
     * @param requestDTO Dados do check-in
     * @return DTO com dados do check-in criado
     * @throws ResourceNotFoundException se o usuário não existir
     * @throws BusinessValidationException se houver violação de regra de negócio
     */
    @Transactional
    @Override
    public CheckinResponseDTO criarCheckin(CheckinRequestDTO requestDTO) {
        // Validação: Usuário existe?
        Usuario usuario = usuarioRepository.findById(requestDTO.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuário com ID " + requestDTO.usuarioId() + " não encontrado"
                ));

        // Validação: Diário não contém conteúdo malicioso
        validarConteudoDiario(requestDTO.diario());

        // Criar entidade Checkin
        Checkin checkin = new Checkin(
                usuario,
                requestDTO.tarefaConcluida(),
                requestDTO.diario()
        );

        // Persistir no banco
        Checkin checkinSalvo = checkinRepository.save(checkin);

        // Retornar DTO de resposta
        return new CheckinResponseDTO(checkinSalvo);
    }

    /**
     * Busca histórico de check-ins de um usuário.
     * 
     * @param usuarioId ID do usuário
     * @return Lista de check-ins ordenados por data
     * @throws ResourceNotFoundException se o usuário não existir
     */
    @Transactional(readOnly = true)
    @Override
    public List<HistoricoResponseDTO> buscarHistorico(Long usuarioId) {
        // Validação: Usuário existe?
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException(
                    "Usuário com ID " + usuarioId + " não encontrado"
            );
        }

        // Buscar check-ins do usuário
        List<Checkin> checkins = checkinRepository.findByUsuarioIdOrderByDataCheckinDesc(usuarioId);

        // Converter para DTO
        return checkins.stream()
                .map(checkin -> new HistoricoResponseDTO(
                        checkin.getDataCheckin(),
                        checkin.getTarefaConcluida(),
                        checkin.getDiario()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Busca histórico de check-ins de um usuário em um período.
     * 
     * @param usuarioId ID do usuário
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @return Lista de check-ins no período
     * @throws ResourceNotFoundException se o usuário não existir
     * @throws BusinessValidationException se as datas forem inválidas
     */
    @Transactional(readOnly = true)
    @Override
    public List<HistoricoResponseDTO> buscarHistoricoPorPeriodo(
            Long usuarioId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        // Validação: Usuário existe?
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException(
                    "Usuário com ID " + usuarioId + " não encontrado"
            );
        }

        // Validação: Datas são válidas?
        validarPeriodo(dataInicio, dataFim);

        // Buscar check-ins no período
        List<Checkin> checkins = checkinRepository.findByUsuarioIdAndDataCheckinBetween(
                usuarioId,
                dataInicio,
                dataFim
        );

        // Converter para DTO
        return checkins.stream()
                .map(checkin -> new HistoricoResponseDTO(
                        checkin.getDataCheckin(),
                        checkin.getTarefaConcluida(),
                        checkin.getDiario()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Valida o conteúdo do diário para prevenir injeções.
     * 
     * Remove ou valida caracteres potencialmente perigosos.
     * 
     * @param diario Conteúdo do diário
     * @throws BusinessValidationException se o conteúdo for inválido
     */
    private void validarConteudoDiario(String diario) {
        if (diario == null || diario.trim().isEmpty()) {
            throw new BusinessValidationException("Diário não pode estar vazio");
        }

        // Validação: Detectar tentativas de SQL Injection
        String diarioLower = diario.toLowerCase();
        String[] sqlKeywords = {
                "drop table", "delete from", "insert into", "update ",
                "select * from", "union select", "--", "/*", "*/"
        };

        for (String keyword : sqlKeywords) {
            if (diarioLower.contains(keyword)) {
                throw new BusinessValidationException(
                        "Conteúdo do diário contém padrões não permitidos"
                );
            }
        }

        // Validação: Detectar tentativas de XSS
        if (diario.contains("<script>") || diario.contains("javascript:") || 
            diario.contains("onerror=") || diario.contains("onload=")) {
            throw new BusinessValidationException(
                    "Conteúdo do diário contém código potencialmente malicioso"
            );
        }
    }

    /**
     * Valida se o período de datas é válido.
     * 
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @throws BusinessValidationException se as datas forem inválidas
     */
    private void validarPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new BusinessValidationException("Datas de início e fim são obrigatórias");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new BusinessValidationException(
                    "Data de início não pode ser posterior à data de fim"
            );
        }

        if (dataFim.isAfter(LocalDateTime.now())) {
            throw new BusinessValidationException(
                    "Data de fim não pode ser no futuro"
            );
        }
    }
}
