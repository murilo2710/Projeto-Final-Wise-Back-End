package com.wise.sistema_gestao_consultas_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelarConsultaRequest {

    @NotBlank(message = "Motivo de cancelamento e obrigatorio")
    @Size(min = 5, message = "Motivo de cancelamento deve ter no minimo 5 caracteres")
    @Size(max = 500, message = "Motivo de cancelamento deve ter no maximo 500 caracteres")
    private String motivoCancelamento;
}
