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
public class EstoqueDashboardResponse {

    private long totalMateriais;
    private long totalMateriaisAtivos;
    private long totalMateriaisInativos;
    private long totalMateriaisBaixoEstoque;
    private long totalMovimentacoes;
    private long totalEntradas;
    private long totalSaidas;
    private long totalAjustes;
    private List<MaterialResponse> materiaisBaixoEstoque;
    private List<MovimentacaoEstoqueResponse> ultimasMovimentacoes;
}
