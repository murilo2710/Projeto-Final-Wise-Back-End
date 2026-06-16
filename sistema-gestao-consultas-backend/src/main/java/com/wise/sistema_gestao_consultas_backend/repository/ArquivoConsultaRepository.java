package com.wise.sistema_gestao_consultas_backend.repository;

import com.wise.sistema_gestao_consultas_backend.entity.ArquivoConsulta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoConsultaRepository extends JpaRepository<ArquivoConsulta, Long> {

    List<ArquivoConsulta> findByConsultaIdOrderByDataUploadDesc(Long consultaId);

    List<ArquivoConsulta> findByConsultaId(Long consultaId);
}
