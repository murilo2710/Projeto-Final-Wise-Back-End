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
public class AdminDashboardResponse {

    private long totalUsuarios;
    private long usuariosAtivos;
    private long usuariosInativos;
    private long usuariosAdmin;
    private long usuariosDentista;
    private long totalPacientes;
    private long totalDentistas;
    private long dentistasAtivos;
    private long dentistasInativos;
    private long totalConsultas;
    private long consultasAgendadas;
    private long consultasFinalizadas;
    private long consultasCanceladas;
    private long totalMateriais;
    private long materiaisBaixoEstoque;
    private long totalMovimentacoesEstoque;
    private long totalLogsAtividade;
    private List<LogAtividadeResponse> ultimosLogs;
}
