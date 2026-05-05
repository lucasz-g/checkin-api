package com.checkin.api.controller;

import com.checkin.api.model.Usuario;
import com.checkin.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * POST /api/usuarios - Cria um novo usuário.
     * 
     * @param usuario Dados do usuário
     * @return Usuário criado com status 201
     */
    @PostMapping
    @Operation(summary = "Criar usuário", description = "Registra um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já em uso")
    })
    public ResponseEntity<Usuario> criarUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario usuarioCriado = usuarioService.criarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    /**
     * GET /api/usuarios/{id} - Busca um usuário por ID.
     * 
     * @param id ID do usuário
     * @return Usuário encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os dados de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Usuario> buscarPorId(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long id
    ) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * GET /api/usuarios - Lista todos os usuários.
     * 
     * @return Lista de usuários
     */
    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/usuarios/email - Busca um usuário por email.
     * 
     * @param email Email do usuário
     * @return Usuário encontrado
     */
    @GetMapping("/email")
    @Operation(summary = "Buscar usuário por email", description = "Retorna os dados de um usuário pelo email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Usuario> buscarPorEmail(
            @Parameter(description = "Email do usuário", required = true)
            @RequestParam String email
    ) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    /**
     * PUT /api/usuarios/{id} - Atualiza dados de um usuário.
     * 
     * @param id ID do usuário
     * @param usuario Dados atualizados
     * @return Usuário atualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Usuario> atualizarUsuario(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long id,
            @Valid @RequestBody Usuario usuario
    ) {
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    /**
     * DELETE /api/usuarios/{id} - Deleta um usuário.
     * 
     * @param id ID do usuário
     * @return Status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> deletarUsuario(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable Long id
    ) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
