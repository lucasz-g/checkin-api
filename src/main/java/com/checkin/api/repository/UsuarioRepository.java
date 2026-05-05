package com.checkin.api.repository;

import com.checkin.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operações de persistência de Usuário.
 * 
 * Camada de acesso a dados seguindo padrão Repository do Spring Data JPA.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo email.
     * 
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se existe um usuário com o email informado.
     * 
     * @param email Email a ser verificado
     * @return true se existir, false caso contrário
     */
    boolean existsByEmail(String email);
}
