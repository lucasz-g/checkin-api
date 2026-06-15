package com.checkin.api.service;

import com.checkin.api.exception.BusinessValidationException;
import com.checkin.api.exception.ResourceNotFoundException;
import com.checkin.api.model.Usuario;
import com.checkin.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void criarUsuarioSalvaQuandoEmailNaoExiste() {
        Usuario usuario = usuario("Ana Souza", "ana@example.com");

        when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(false);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.criarUsuario(usuario);

        assertThat(resultado).isSameAs(usuario);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void criarUsuarioRejeitaEmailDuplicado() {
        Usuario usuario = usuario("Ana Souza", "ana@example.com");

        when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.criarUsuario(usuario))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("em uso");

        verify(usuarioRepository, never()).save(usuario);
    }

    @Test
    void buscarPorIdLancaExcecaoQuandoNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void atualizarUsuarioValidaEmailDeOutroUsuario() {
        Usuario existente = usuario("Ana Souza", "ana@example.com");
        existente.setId(1L);

        Usuario atualizado = usuario("Ana Silva", "novo@example.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmail("novo@example.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.atualizarUsuario(1L, atualizado))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("em uso");

        verify(usuarioRepository, never()).save(existente);
    }

    private Usuario usuario(String nome, String email) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha("senha123");
        return usuario;
    }
}
