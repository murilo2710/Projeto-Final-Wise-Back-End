package com.wise.sistema_gestao_consultas_backend.dto.request;

import com.wise.sistema_gestao_consultas_backend.enums.TipoMovimentacaoEstoque;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoqueRequest {

    @NotNull(message = "Material e obrigatorio")
    private Long materialId;

    @NotNull(message = "Tipo de movimentacao e obrigatorio")
    private TipoMovimentacaoEstoque tipo;

    @NotNull(message = "Quantidade e obrigatoria")
    @DecimalMin(value = "0.00", inclusive = false, message = "Quantidade deve ser maior que zero")
    private BigDecimal quantidade;

    @NotBlank(message = "Motivo e obrigatorio")
    @Size(min = 5, message = "Motivo deve ter no minimo 5 caracteres")
    @Size(max = 500, message = "Motivo deve ter no maximo 500 caracteres")
    private String motivo;
}
