package com.wise.sistema_gestao_consultas_backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRequest {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres")
    private String nome;

    @Size(max = 500, message = "Descricao deve ter no maximo 500 caracteres")
    private String descricao;

    @NotBlank(message = "Unidade de medida e obrigatoria")
    @Size(max = 30, message = "Unidade de medida deve ter no maximo 30 caracteres")
    private String unidadeMedida;

    @NotNull(message = "Quantidade atual e obrigatoria")
    @DecimalMin(value = "0.00", message = "Quantidade atual nao pode ser negativa")
    private BigDecimal quantidadeAtual;

    @NotNull(message = "Quantidade minima e obrigatoria")
    @DecimalMin(value = "0.00", message = "Quantidade minima nao pode ser negativa")
    private BigDecimal quantidadeMinima;

    private Boolean ativo;

    private List<Long> especialidadeIds;
}
