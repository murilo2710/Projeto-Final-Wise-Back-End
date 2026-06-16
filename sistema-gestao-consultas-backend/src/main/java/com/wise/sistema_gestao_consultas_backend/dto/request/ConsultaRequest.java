package com.wise.sistema_gestao_consultas_backend.dto.request;

import com.wise.sistema_gestao_consultas_backend.enums.StatusConsulta;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaRequest {

    @NotNull(message = "Paciente e obrigatorio")
    private Long pacienteId;

    @NotNull(message = "Dentista e obrigatorio")
    private Long dentistaId;

    private Long usuarioId;

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(min = 5, message = "Descricao deve ter no minimo 5 caracteres")
    @Size(max = 500, message = "Descricao deve ter no maximo 500 caracteres")
    private String descricao;

    @Size(max = 500, message = "Motivo de cancelamento deve ter no maximo 500 caracteres")
    private String motivoCancelamento;

    @NotNull(message = "Data de inicio e obrigatoria")
    @FutureOrPresent(message = "Nao e permitido agendar consultas no passado")
    private LocalDateTime dataInicio;

    @NotNull(message = "Data de fim e obrigatoria")
    private LocalDateTime dataFim;

    private StatusConsulta status;
}
