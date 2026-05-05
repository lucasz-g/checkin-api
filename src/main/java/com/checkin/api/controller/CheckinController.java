package com.checkin.api.controller;

import com.checkin.api.dto.CheckinRequestDTO;
import com.checkin.api.dto.CheckinResponseDTO;
import com.checkin.api.dto.HistoricoResponseDTO;
import com.checkin.api.service.CheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api")
@Tag(name = "Check-in", description = "Endpoints para gerenciamento de check-ins de tarefas")
public class CheckinController {

    private final CheckinService checkinService;

    public CheckinController(CheckinService checkinService) {
        this.checkinService = checkinService;
    }

    /**
     * POST /api/checkin - Cria um novo check-in.
     * 
     * @param requestDTO Dados do check-in
     * @return Check-in criado com status 201
     */
    @PostMapping("/checkin")
    @Operation(summary = "Criar check-in", description = "Registra um novo check-in de tarefa com status e diário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Check-in criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<CheckinResponseDTO> criarCheckin(
            @Valid @RequestBody CheckinRequestDTO requestDTO
    ) {
        CheckinResponseDTO response = checkinService.criarCheckin(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/historico - Busca histórico de check-ins de um usuário.
     * 
     * @param usuarioId ID do usuário
     * @return Lista de check-ins do usuário
     */
    @GetMapping("/historico")
    @Operation(summary = "Buscar histórico", description = "Retorna todos os check-ins de um usuário para gráficos de evolução")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<HistoricoResponseDTO>> buscarHistorico(
            @Parameter(description = "ID do usuário", required = true)
            @RequestParam Long usuarioId
    ) {
        List<HistoricoResponseDTO> historico = checkinService.buscarHistorico(usuarioId);
        return ResponseEntity.ok(historico);
    }

    /**
     * GET /api/historico/periodo - Busca histórico de check-ins em um período.
     * 
     * @param usuarioId ID do usuário
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @return Lista de check-ins no período
     */
    @GetMapping("/historico/periodo")
    @Operation(summary = "Buscar histórico por período", description = "Retorna check-ins de um usuário em um período específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Período inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<HistoricoResponseDTO>> buscarHistoricoPorPeriodo(
            @Parameter(description = "ID do usuário", required = true)
            @RequestParam Long usuarioId,
            @Parameter(description = "Data de início (formato: yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @Parameter(description = "Data de fim (formato: yyyy-MM-dd'T'HH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim
    ) {
        List<HistoricoResponseDTO> historico = checkinService.buscarHistoricoPorPeriodo(
                usuarioId,
                dataInicio,
                dataFim
        );
        return ResponseEntity.ok(historico);
    }
}
