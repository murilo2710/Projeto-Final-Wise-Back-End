package com.wise.sistema_gestao_consultas_backend.repository;

import com.wise.sistema_gestao_consultas_backend.entity.Consulta;
import com.wise.sistema_gestao_consultas_backend.enums.StatusConsulta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("""
            select c from Consulta c
            join fetch c.paciente
            join fetch c.dentista
            join fetch c.usuario
            """)
    List<Consulta> findAllComRelacionamentos();

    @Query("""
            select c from Consulta c
            join fetch c.paciente
            join fetch c.dentista
            join fetch c.usuario
            where c.usuario.id = :usuarioId
            """)
    List<Consulta> findAllComRelacionamentosPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("""
            select c from Consulta c
            join fetch c.paciente
            join fetch c.dentista
            join fetch c.usuario
            where c.id = :id
            """)
    Optional<Consulta> findByIdComRelacionamentos(@Param("id") Long id);

    @Query("""
            select c from Consulta c
            join fetch c.paciente
            join fetch c.dentista
            join fetch c.usuario
            where c.id = :id and c.usuario.id = :usuarioId
            """)
    Optional<Consulta> findByIdComRelacionamentosPorUsuario(
            @Param("id") Long id,
            @Param("usuarioId") Long usuarioId
    );

    @Query("""
            select count(c) > 0 from Consulta c
            where c.dentista.id = :dentistaId
              and c.status <> :statusCancelada
              and (:consultaId is null or c.id <> :consultaId)
              and c.dataInicio < :dataFim
              and c.dataFim > :dataInicio
            """)
    boolean existeConflitoHorario(
            @Param("dentistaId") Long dentistaId,
            @Param("statusCancelada") StatusConsulta statusCancelada,
            @Param("consultaId") Long consultaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
