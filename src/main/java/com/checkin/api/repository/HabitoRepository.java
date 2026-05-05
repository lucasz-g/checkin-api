package com.checkin.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.checkin.api.model.Habito;

/**
 * Repositório para a entidade Habito. Permite operações CRUD e consulta
 * específica por usuário.
 */
public interface HabitoRepository extends JpaRepository<Habito, Long> {

    /**
     * Busca os hábitos associados a um usuário específico.
     *
     * @param usuarioId identificador do usuário
     * @return lista de hábitos pertencentes ao usuário
     */
    @Query("SELECT h FROM Habito h WHERE h.usuario.id = :usuarioId")
    List<Habito> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}