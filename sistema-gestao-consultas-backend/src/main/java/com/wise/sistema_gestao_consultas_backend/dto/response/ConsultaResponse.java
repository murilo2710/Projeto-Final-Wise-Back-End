package com.wise.sistema_gestao_consultas_backend.dto.response;

import com.wise.sistema_gestao_consultas_backend.enums.StatusConsulta;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaResponse {

    private Long id;
    private Long pacienteId;
    private Long dentistaId;
    private Long usuarioId;
    private String descricao;
    private String motivoCancelamento;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private LocalDateTime dataRegistro;
    private StatusConsulta status;
}
