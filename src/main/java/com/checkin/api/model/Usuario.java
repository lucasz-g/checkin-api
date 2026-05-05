package com.checkin.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.checkin.api.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    /**
     * Senha do usuário. A senha é armazenada de forma segura utilizando
     * BCrypt. Este campo é anotado com @JsonIgnore para evitar que seja
     * serializado nas respostas da API.
     */
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 120, message = "Senha deve ter entre 6 e 120 caracteres")
    @Column(nullable = false, length = 120)
    @JsonIgnore
    private String senha;

    /**
     * Papel do usuário na aplicação. Por padrão todo usuário novo terá o papel
     * USER. Usuários com papel ADMIN poderão acessar endpoints administrativos.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Checkin> checkins = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }
}
