package com.wise.sistema_gestao_consultas_backend.dto.response;

import com.wise.sistema_gestao_consultas_backend.enums.TipoMovimentacaoEstoque;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoqueResponse {

    private Long id;
    private Long materialId;
    private String materialNome;
    private Long usuarioId;
    private String usuarioNome;
    private TipoMovimentacaoEstoque tipo;
    private BigDecimal quantidade;
    private BigDecimal estoqueAnterior;
    private BigDecimal estoqueAtual;
    private String motivo;
    private LocalDateTime dataMovimentacao;
}
