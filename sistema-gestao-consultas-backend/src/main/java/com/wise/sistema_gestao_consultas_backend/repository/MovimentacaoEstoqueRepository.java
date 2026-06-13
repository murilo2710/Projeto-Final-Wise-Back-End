package com.wise.sistema_gestao_consultas_backend.repository;

import com.wise.sistema_gestao_consultas_backend.entity.MovimentacaoEstoque;
import com.wise.sistema_gestao_consultas_backend.enums.TipoMovimentacaoEstoque;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    @Query("""
            select m from MovimentacaoEstoque m
            join fetch m.material
            join fetch m.usuario
            where (:materialId is null or m.material.id = :materialId)
              and (:tipo is null or m.tipo = :tipo)
              and (:dataInicio is null or m.dataMovimentacao >= :dataInicio)
              and (:dataFim is null or m.dataMovimentacao <= :dataFim)
            order by m.dataMovimentacao desc
            """)
    List<MovimentacaoEstoque> buscarComFiltros(
            @Param("materialId") Long materialId,
            @Param("tipo") TipoMovimentacaoEstoque tipo,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}
