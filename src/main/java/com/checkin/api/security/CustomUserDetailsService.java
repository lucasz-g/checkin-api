package com.checkin.api.security;

import com.checkin.api.model.Usuario;
import com.checkin.api.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * Implementação de {@link UserDetailsService} responsável por carregar
 * informações do usuário a partir do email (username) para autenticação.
 *
 * Utiliza o {@link UsuarioRepository} para buscar o usuário e converte
 * seu papel em authorities do Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário com email " + username + " não encontrado"));

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                getAuthorities(usuario)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()));
    }
}