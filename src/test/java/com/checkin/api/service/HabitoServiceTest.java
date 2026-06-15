package com.checkin.api.service;

import com.checkin.api.dto.HabitoRequestDTO;
import com.checkin.api.dto.HabitoResponseDTO;
import com.checkin.api.exception.ResourceNotFoundException;
import com.checkin.api.model.Habito;
import com.checkin.api.model.Usuario;
import com.checkin.api.repository.HabitoRepository;
import com.checkin.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HabitoServiceTest {

    @Mock
    private HabitoRepository habitoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private HabitoService habitoService;

    @Test
    void criarHabitoAssociaAoUsuarioEMapeiaResposta() {
        Usuario usuario = usuario();
        HabitoRequestDTO request = new HabitoRequestDTO("Beber agua", "Beber agua durante o dia", "2 litros");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(habitoRepository.save(org.mockito.ArgumentMatchers.any(Habito.class))).thenAnswer(invocation -> {
            Habito habito = invocation.getArgument(0);
            habito.setId(5L);
            habito.setDataCriacao(LocalDateTime.of(2026, 6, 4, 8, 0));
            return habito;
        });

        HabitoResponseDTO response = habitoService.criarHabito(1L, request);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.nome()).isEqualTo("Beber agua");
        assertThat(response.meta()).isEqualTo("2 litros");
    }

    @Test
    void listarHabitosExigeUsuarioExistente() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> habitoService.listarHabitosPorUsuario(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void listarHabitosMapeiaEntidadesParaDto() {
        Habito habito = habito("Alongar", "Alongamento pela manha", "10 minutos");
        habito.setId(7L);
        habito.setDataCriacao(LocalDateTime.of(2026, 6, 4, 7, 30));

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(habitoRepository.findByUsuarioId(1L)).thenReturn(List.of(habito));

        List<HabitoResponseDTO> response = habitoService.listarHabitosPorUsuario(1L);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).nome()).isEqualTo("Alongar");
    }

    @Test
    void deletarHabitoRemoveEntidadeExistente() {
        Habito habito = habito("Alongar", "Alongamento pela manha", "10 minutos");
        habito.setId(7L);

        when(habitoRepository.findById(7L)).thenReturn(Optional.of(habito));

        habitoService.deletarHabito(7L);

        verify(habitoRepository).delete(habito);
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Ana Souza");
        usuario.setEmail("ana@example.com");
        usuario.setSenha("senha123");
        return usuario;
    }

    private Habito habito(String nome, String descricao, String meta) {
        Habito habito = new Habito();
        habito.setNome(nome);
        habito.setDescricao(descricao);
        habito.setMeta(meta);
        habito.setUsuario(usuario());
        return habito;
    }
}
