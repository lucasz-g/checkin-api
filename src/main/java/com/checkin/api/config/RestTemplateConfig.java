package com.checkin.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuração responsável por expor um bean de {@link RestTemplate} para
 * consumir serviços REST externos. Pode ser personalizado para incluir
 * interceptadores, timeouts e balanceamento de carga em futuras melhorias.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}