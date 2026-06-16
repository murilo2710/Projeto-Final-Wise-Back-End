package com.wise.sistema_gestao_consultas_backend.repository;

import com.wise.sistema_gestao_consultas_backend.entity.LogAtividade;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LogAtividadeRepository extends JpaRepository<LogAtividade, Long> {

    List<LogAtividade> findTop10ByOrderByDataCriacaoDesc();

    @Query("""
            select l from LogAtividade l
            where (:usuarioId is null or l.usuarioId = :usuarioId)
              and (:tipo is null or l.tipo = :tipo)
              and (:recurso is null or l.recurso = :recurso)
              and (:dataInicio is null or l.dataCriacao >= :dataInicio)
              and (:dataFim is null or l.dataCriacao <= :dataFim)
            order by l.dataCriacao desc
            """)
    List<LogAtividade> buscarComFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") String tipo,
            @Param("recurso") String recurso,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );
}
