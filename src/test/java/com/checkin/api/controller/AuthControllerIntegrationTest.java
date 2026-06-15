package com.checkin.api.controller;

import com.checkin.api.dto.CheckinRequestDTO;
import com.checkin.api.dto.LoginRequestDTO;
import com.checkin.api.dto.RegisterRequestDTO;
import com.checkin.api.model.Usuario;
import com.checkin.api.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerCriptografaSenhaERetornaUsuarioSemSenha() throws Exception {
        String email = emailUnico();
        RegisterRequestDTO request = new RegisterRequestDTO("Ana Souza", email, "senha123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.senha").doesNotExist());

        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        assertThat(usuario.getSenha()).isNotEqualTo("senha123");
        assertThat(passwordEncoder.matches("senha123", usuario.getSenha())).isTrue();
    }

    @Test
    void loginRetornaJwtParaCredenciaisValidas() throws Exception {
        String email = emailUnico();
        registrarUsuario(email, "senha123");

        LoginRequestDTO request = new LoginRequestDTO(email, "senha123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void endpointProtegidoSemTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenJwtPermiteAcessarEndpointProtegido() throws Exception {
        String email = emailUnico();
        long usuarioId = registrarUsuario(email, "senha123");
        String token = login(email, "senha123");

        CheckinRequestDTO request = new CheckinRequestDTO(
                usuarioId,
                true,
                "Hoje completei uma rotina saudavel e registrei o progresso."
        );

        mockMvc.perform(post("/api/checkin")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(usuarioId))
                .andExpect(jsonPath("$.tarefaConcluida").value(true));
    }

    private long registrarUsuario(String email, String senha) throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("Ana Souza", email, senha);
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }

    private String login(String email, String senha) throws Exception {
        LoginRequestDTO request = new LoginRequestDTO(email, senha);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }

    private String emailUnico() {
        return "user-" + UUID.randomUUID() + "@example.com";
    }
}
