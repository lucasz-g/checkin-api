package com.checkin.api.service;

import com.checkin.api.dto.SugestaoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SugestaoServiceTest {

    private static final String URL = "https://example.test/sugestoes";

    private RestTemplate restTemplate;
    private SugestaoService sugestaoService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        sugestaoService = new SugestaoService(restTemplate);
        ReflectionTestUtils.setField(sugestaoService, "sugestaoApiUrl", URL);
    }

    @Test
    void buscarSugestaoRetornaAtividadeDaApiExterna() {
        when(restTemplate.getForEntity(URL, String.class))
                .thenReturn(ResponseEntity.ok("{\"activity\":\"Walk outside\"}"));

        SugestaoResponseDTO response = sugestaoService.buscarSugestao();

        assertThat(response.sugestao()).isEqualTo("Walk outside");
    }

    @Test
    void buscarSugestaoUsaFallbackQuandoApiFalha() {
        when(restTemplate.getForEntity(URL, String.class))
                .thenThrow(new RestClientException("timeout"));

        SugestaoResponseDTO response = sugestaoService.buscarSugestao();

        assertThat(response.sugestao()).contains("Beba");
    }
}
