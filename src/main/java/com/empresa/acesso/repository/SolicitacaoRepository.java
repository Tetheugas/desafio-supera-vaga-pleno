package com.empresa.acesso.repository;

import com.empresa.acesso.entity.Solicitacao;
import com.empresa.acesso.entity.enums.StatusSolicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    
    @Query("SELECT s FROM Solicitacao s WHERE s.usuario.id = :usuarioId")
    Page<Solicitacao> findByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);
    
    @Query(value = "SELECT DISTINCT s.* FROM solicitacoes s " +
           "LEFT JOIN solicitacao_modulos sm ON s.id = sm.solicitacao_id " +
           "LEFT JOIN modulos m ON sm.modulo_id = m.id " +
           "WHERE s.usuario_id = :usuarioId " +
           "AND (:texto IS NULL OR s.protocolo ILIKE CONCAT('%', CAST(:texto AS text), '%') " +
           "     OR m.nome ILIKE CONCAT('%', CAST(:texto AS text), '%') " +
           "     OR CAST(s.justificativa AS text) ILIKE CONCAT('%', CAST(:texto AS text), '%')) " +
           "AND (:status IS NULL OR s.status = CAST(:status AS text)) " +
           "AND (:urgente IS NULL OR s.urgente = :urgente) " +
           "AND (:dataInicio IS NULL OR s.data_solicitacao >= CAST(:dataInicio AS timestamp)) " +
           "AND (:dataFim IS NULL OR s.data_solicitacao <= CAST(:dataFim AS timestamp)) " +
           "ORDER BY s.data_solicitacao DESC",
           countQuery = "SELECT COUNT(DISTINCT s.id) FROM solicitacoes s " +
           "LEFT JOIN solicitacao_modulos sm ON s.id = sm.solicitacao_id " +
           "LEFT JOIN modulos m ON sm.modulo_id = m.id " +
           "WHERE s.usuario_id = :usuarioId " +
           "AND (:texto IS NULL OR s.protocolo ILIKE CONCAT('%', CAST(:texto AS text), '%') " +
           "     OR m.nome ILIKE CONCAT('%', CAST(:texto AS text), '%') " +
           "     OR CAST(s.justificativa AS text) ILIKE CONCAT('%', CAST(:texto AS text), '%')) " +
           "AND (:status IS NULL OR s.status = CAST(:status AS text)) " +
           "AND (:urgente IS NULL OR s.urgente = :urgente) " +
           "AND (:dataInicio IS NULL OR s.data_solicitacao >= CAST(:dataInicio AS timestamp)) " +
           "AND (:dataFim IS NULL OR s.data_solicitacao <= CAST(:dataFim AS timestamp))",
           nativeQuery = true)
    Page<Solicitacao> findByUsuarioIdWithFilters(
        @Param("usuarioId") Long usuarioId,
        @Param("texto") String texto,
        @Param("status") String status,
        @Param("urgente") Boolean urgente,
        @Param("dataInicio") String dataInicio,
        @Param("dataFim") String dataFim,
        Pageable pageable
    );
    
    @Query("SELECT s FROM Solicitacao s WHERE s.id = :id AND s.usuario.id = :usuarioId")
    Optional<Solicitacao> findByIdAndUsuarioId(@Param("id") Long id, @Param("usuarioId") Long usuarioId);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Solicitacao s " +
           "JOIN s.modulos m WHERE s.usuario.id = :usuarioId AND m.id = :moduloId " +
           "AND s.status = 'ATIVO'")
    boolean existsSolicitacaoAtivaParaModulo(@Param("usuarioId") Long usuarioId, @Param("moduloId") Long moduloId);
}
