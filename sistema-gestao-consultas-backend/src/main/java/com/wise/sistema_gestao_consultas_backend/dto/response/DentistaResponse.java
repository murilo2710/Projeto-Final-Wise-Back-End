package com.wise.sistema_gestao_consultas_backend.dto.response;

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
public class DentistaResponse {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String cro;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private List<EspecialidadeResponse> especialidades;
}
