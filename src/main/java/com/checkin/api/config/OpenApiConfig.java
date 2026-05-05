package com.checkin.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI (Swagger) para documentação da API.
 * 
 * Define metadados e informações sobre a API RESTful.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CheckIn API")
                        .version("1.0.0")
                        .description("API RESTful para gerenciamento de check-ins de tarefas e histórico de evolução. " +
                                "Implementa arquitetura SOA com separação de camadas (Controller, Service, Repository), " +
                                "validação de entrada para segurança e integração com banco de dados.")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("contato@checkin.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api.checkin.com")
                                .description("Servidor de Produção")
                ));
    }
}
