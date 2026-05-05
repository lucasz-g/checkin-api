package com.checkin.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "checkins")
public class Checkin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "Status da tarefa é obrigatório")
    @Column(name = "tarefa_concluida", nullable = false)
    private Boolean tarefaConcluida;

    @NotBlank(message = "Diário/motivo é obrigatório")
    @Size(min = 10, max = 1000, message = "Diário deve ter entre 10 e 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String diario;

    @Column(name = "data_checkin", nullable = false)
    private LocalDateTime dataCheckin;

    @PrePersist
    protected void onCreate() {
        if (dataCheckin == null) {
            dataCheckin = LocalDateTime.now();
        }
    }

    public Checkin(Usuario usuario, Boolean tarefaConcluida, String diario) {
        this.usuario = usuario;
        this.tarefaConcluida = tarefaConcluida;
        this.diario = diario;
    }
}
