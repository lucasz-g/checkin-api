package com.checkin.api.controller;

import com.checkin.api.dto.SugestaoResponseDTO;
import com.checkin.api.service.SugestaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador que expõe um endpoint para obter sugestões de hábitos de saúde.
 * O endpoint consome um serviço externo através do {@link SugestaoService}.
 */
@RestController
@RequestMapping("/api/sugestoes")
@Tag(name = "Sugestões", description = "Endpoints para sugestões de hábitos de saúde via API externa")
public class SugestaoController {

    private final SugestaoService sugestaoService;

    public SugestaoController(SugestaoService sugestaoService) {
        this.sugestaoService = sugestaoService;
    }

    /**
     * GET /api/sugestoes/habito - Obtém uma sugestão de hábito saudável.
     *
     * @return sugestão retornada pela API externa
     */
    @GetMapping("/habito")
    @Operation(summary = "Obter sugestão de hábito", description = "Consome uma API externa para retornar uma sugestão de hábito saudável")
    @ApiResponse(responseCode = "200", description = "Sugestão obtida com sucesso")
    public ResponseEntity<SugestaoResponseDTO> obterSugestao() {
        SugestaoResponseDTO dto = sugestaoService.buscarSugestao();
        return ResponseEntity.ok(dto);
    }
}