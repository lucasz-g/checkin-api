package com.checkin.api.service;

import com.checkin.api.dto.CheckinRequestDTO;
import com.checkin.api.dto.CheckinResponseDTO;
import com.checkin.api.dto.HistoricoResponseDTO;
import com.checkin.api.exception.BusinessValidationException;
import com.checkin.api.exception.ResourceNotFoundException;
import com.checkin.api.model.Checkin;
import com.checkin.api.model.Usuario;
import com.checkin.api.repository.CheckinRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckinServiceTest {

    @Mock
    private CheckinRepository checkinRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CheckinService checkinService;

    @Test
    void criarCheckinSalvaQuandoUsuarioExisteEConteudoEhValido() {
        Usuario usuario = usuario();
        CheckinRequestDTO request = new CheckinRequestDTO(
                1L,
                true,
                "Hoje finalizei minhas tarefas principais com foco."
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(checkinRepository.save(any(Checkin.class))).thenAnswer(invocation -> {
            Checkin checkin = invocation.getArgument(0);
            checkin.setId(10L);
            checkin.setDataCheckin(LocalDateTime.of(2026, 6, 4, 10, 0));
            return checkin;
        });

        CheckinResponseDTO response = checkinService.criarCheckin(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.usuarioId()).isEqualTo(1L);
        assertThat(response.usuarioNome()).isEqualTo("Ana Souza");
        assertThat(response.tarefaConcluida()).isTrue();
        verify(checkinRepository).save(any(Checkin.class));
    }

    @Test
    void criarCheckinRejeitaConteudoMalicioso() {
        CheckinRequestDTO request = new CheckinRequestDTO(
                1L,
                true,
                "Texto valido antes de drop table usuarios"
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));

        assertThatThrownBy(() -> checkinService.criarCheckin(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("permitidos");

        verify(checkinRepository, never()).save(any(Checkin.class));
    }

    @Test
    void buscarHistoricoLancaExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> checkinService.buscarHistorico(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void buscarHistoricoPorPeriodoValidaOrdemDasDatas() {
        LocalDateTime dataInicio = LocalDateTime.of(2026, 6, 4, 10, 0);
        LocalDateTime dataFim = LocalDateTime.of(2026, 6, 3, 10, 0);

        when(usuarioRepository.existsById(1L)).thenReturn(true);

        assertThatThrownBy(() -> checkinService.buscarHistoricoPorPeriodo(1L, dataInicio, dataFim))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("posterior");

        verify(checkinRepository, never()).findByUsuarioIdAndDataCheckinBetween(1L, dataInicio, dataFim);
    }

    @Test
    void buscarHistoricoMapeiaCheckinsParaDto() {
        Checkin checkin = new Checkin(usuario(), true, "Dia produtivo e sem bloqueios relevantes.");
        checkin.setDataCheckin(LocalDateTime.of(2026, 6, 4, 9, 30));

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(checkinRepository.findByUsuarioIdOrderByDataCheckinDesc(1L)).thenReturn(List.of(checkin));

        List<HistoricoResponseDTO> historico = checkinService.buscarHistorico(1L);

        assertThat(historico).hasSize(1);
        assertThat(historico.get(0).tarefaConcluida()).isTrue();
        assertThat(historico.get(0).diario()).isEqualTo("Dia produtivo e sem bloqueios relevantes.");
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Ana Souza");
        usuario.setEmail("ana@example.com");
        usuario.setSenha("senha123");
        return usuario;
    }
}
