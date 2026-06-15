package com.checkin.api.controller;

import com.checkin.api.dto.HabitoRequestDTO;
import com.checkin.api.dto.HabitoResponseDTO;
import com.checkin.api.service.HabitoServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gerenciamento de hábitos de saúde. Permite criar,
 * listar e deletar hábitos relacionados a um usuário específico. Todos
 * os endpoints exigem autenticação via JWT.
 */
@RestController
@RequestMapping("/api/habitos")
@Tag(name = "Hábitos", description = "Endpoints para gerenciamento de hábitos de saúde")
@SecurityRequirement(name = "bearerAuth")
public class HabitoController {

    private final HabitoServicePort habitoService;

    public HabitoController(HabitoServicePort habitoService) {
        this.habitoService = habitoService;
    }

    /**
     * POST /api/habitos/{usuarioId} - Cria um novo hábito para o usuário.
     *
     * @param usuarioId identificador do usuário
     * @param requestDTO dados do hábito a ser criado
     * @return hábito criado
     */
    @PostMapping("/{usuarioId}")
    @Operation(summary = "Criar hábito", description = "Registra um novo hábito de saúde para o usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hábito criado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<HabitoResponseDTO> criarHabito(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long usuarioId,
            @Valid @RequestBody HabitoRequestDTO requestDTO
    ) {
        HabitoResponseDTO response = habitoService.criarHabito(usuarioId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/habitos/{usuarioId} - Lista hábitos de um usuário.
     *
     * @param usuarioId identificador do usuário
     * @return lista de hábitos
     */
    @GetMapping("/{usuarioId}")
    @Operation(summary = "Listar hábitos", description = "Retorna todos os hábitos de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de hábitos retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<HabitoResponseDTO>> listarHabitos(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long usuarioId
    ) {
        List<HabitoResponseDTO> habitos = habitoService.listarHabitosPorUsuario(usuarioId);
        return ResponseEntity.ok(habitos);
    }

    /**
     * DELETE /api/habitos/{habitoId} - Remove um hábito.
     *
     * @param habitoId identificador do hábito
     * @return status 204
     */
    @DeleteMapping("/{habitoId}/deletar")
    @Operation(summary = "Deletar hábito", description = "Remove um hábito existente do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hábito deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Hábito não encontrado")
    })
    public ResponseEntity<Void> deletarHabito(
            @Parameter(description = "ID do hábito", required = true) @PathVariable Long habitoId
    ) {
        habitoService.deletarHabito(habitoId);
        return ResponseEntity.noContent().build();
    }
}
