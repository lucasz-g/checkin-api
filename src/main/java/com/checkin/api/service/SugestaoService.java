package com.checkin.api.service;

import com.checkin.api.dto.SugestaoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serviço responsável por consumir uma API externa e fornecer sugestões de
 * hábitos de saúde. Nesta implementação utiliza a Bored API para
 * retornar atividades recreativas que podem ser convertidas em hábitos
 * saudáveis.
 */
@Service
public class SugestaoService implements SugestaoServicePort {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * URL da API externa utilizada para obter sugestões. Pode ser
     * configurada no application.properties para facilitar troca de APIs.
     */
    @Value("${sugestao.api.url:https://bored-api.appbrewery.com/filter?type=recreational}")
    private String sugestaoApiUrl;

    public SugestaoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
        // Inicializa o ObjectMapper para processar JSON
    }

    /**
     * Busca uma sugestão de atividade na API externa e converte para DTO.
     *
     * @return DTO contendo a atividade sugerida
     */
    @Override
    public SugestaoResponseDTO buscarSugestao() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(sugestaoApiUrl, String.class);
            JsonNode node = objectMapper.readTree(response.getBody());
            String activity = node.has("activity") ? node.get("activity").asText() : "Faça uma caminhada ao ar livre";
            return new SugestaoResponseDTO(activity);
        } catch (RestClientException | java.io.IOException e) {
            // Em caso de falha na API externa, retorna sugestão padrão
            return new SugestaoResponseDTO("Beba água e faça alongamentos leves");
        }
    }
}
