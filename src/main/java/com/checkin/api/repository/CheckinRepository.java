package com.checkin.api.repository;

import com.checkin.api.model.Checkin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para operações de persistência de Checkin.
 * 
 * Camada de acesso a dados seguindo padrão Repository do Spring Data JPA.
 */
@Repository
public interface CheckinRepository extends JpaRepository<Checkin, Long> {

    /**
     * Busca todos os check-ins de um usuário ordenados por data.
     * 
     * @param usuarioId ID do usuário
     * @return Lista de check-ins do usuário
     */
    @Query("SELECT c FROM Checkin c WHERE c.usuario.id = :usuarioId ORDER BY c.dataCheckin DESC")
    List<Checkin> findByUsuarioIdOrderByDataCheckinDesc(@Param("usuarioId") Long usuarioId);

    /**
     * Busca check-ins de um usuário dentro de um período.
     * 
     * @param usuarioId ID do usuário
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @return Lista de check-ins no período
     */
    @Query("SELECT c FROM Checkin c WHERE c.usuario.id = :usuarioId " +
           "AND c.dataCheckin BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY c.dataCheckin DESC")
    List<Checkin> findByUsuarioIdAndDataCheckinBetween(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    /**
     * Conta quantos check-ins um usuário possui.
     * 
     * @param usuarioId ID do usuário
     * @return Quantidade de check-ins
     */
    long countByUsuarioId(Long usuarioId);
}
