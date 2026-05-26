package com.wise.sistema_gestao_consultas_backend.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponse {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private LocalDateTime dataCriacao;
}
