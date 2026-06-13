package com.wise.sistema_gestao_consultas_backend.repository;

import com.wise.sistema_gestao_consultas_backend.entity.Material;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByNome(String nome);

    boolean existsByEspecialidadesId(Long especialidadeId);

    @Query("""
            select distinct m from Material m
            left join fetch m.especialidades e
            where (:ativo is null or m.ativo = :ativo)
              and (:baixoEstoque is null or (
                    (:baixoEstoque = true and m.quantidadeAtual <= m.quantidadeMinima)
                    or (:baixoEstoque = false and m.quantidadeAtual > m.quantidadeMinima)
              ))
              and (:especialidadeId is null or e.id = :especialidadeId)
            order by m.nome
            """)
    List<Material> buscarComFiltros(
            @Param("ativo") Boolean ativo,
            @Param("baixoEstoque") Boolean baixoEstoque,
            @Param("especialidadeId") Long especialidadeId
    );

    @Query("""
            select distinct m from Material m
            left join fetch m.especialidades
            where m.id = :id
            """)
    Optional<Material> findByIdComEspecialidades(@Param("id") Long id);
}
