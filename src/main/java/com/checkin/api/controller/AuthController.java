package com.checkin.api.controller;

import com.checkin.api.dto.AuthResponseDTO;
import com.checkin.api.dto.LoginRequestDTO;
import com.checkin.api.dto.RegisterRequestDTO;
import com.checkin.api.model.Usuario;
import com.checkin.api.model.enums.Role;
import com.checkin.api.security.JwtTokenProvider;
import com.checkin.api.service.UsuarioServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsável pelos endpoints de autenticação e registro de
 * usuários. Permite cadastrar novos usuários e realizar o login
 * retornando um token JWT.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioServicePort usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioServicePort usuarioService,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Endpoint para registrar um novo usuário. Retorna o usuário criado
     * (sem a senha) com status 201.
     *
     * @param request DTO contendo nome, email e senha
     * @return usuário criado
     */
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@Valid @RequestBody RegisterRequestDTO request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setRole(Role.USER);
        Usuario criado = usuarioService.criarUsuario(usuario);
        // Não retornamos a senha pois ela está anotada com @JsonIgnore
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    /**
     * Endpoint de login. Autentica o usuário e retorna um token JWT. Caso
     * o email ou senha sejam inválidos, uma exceção será lançada pelo
     * AuthenticationManager e tratada pelo handler global.
     *
     * @param request DTO contendo email e senha
     * @return token JWT para uso nas próximas requisições
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(request.email());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
