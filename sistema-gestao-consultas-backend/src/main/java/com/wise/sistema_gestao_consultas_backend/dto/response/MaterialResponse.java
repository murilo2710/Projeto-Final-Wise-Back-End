package com.wise.sistema_gestao_consultas_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialResponse {

    private Long id;
    private String nome;
    private String descricao;
    private String unidadeMedida;
    private BigDecimal quantidadeAtual;
    private BigDecimal quantidadeMinima;
    private Boolean ativo;
    private Boolean baixoEstoque;
    private LocalDateTime dataCriacao;
    private List<EspecialidadeResponse> especialidades;
}
