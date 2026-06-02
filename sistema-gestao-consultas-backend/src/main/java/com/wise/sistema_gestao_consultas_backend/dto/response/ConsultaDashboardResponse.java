package com.wise.sistema_gestao_consultas_backend.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDashboardResponse {

    private long totalConsultas;
    private long totalAgendadas;
    private long totalCanceladas;
    private long totalRealizadas;
    private long totalPacientesComConsulta;
    private long totalDentistasComConsulta;
    private List<ConsultaResponse> proximasConsultas;
}
