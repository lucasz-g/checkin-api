package com.checkin.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação CheckIn API.
 * 
 * Esta aplicação implementa uma API RESTful seguindo arquitetura SOA
 * para gerenciamento de check-ins de tarefas e histórico de evolução.
 */
@SpringBootApplication
public class CheckinApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckinApiApplication.class, args);
    }
}
