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
public class LogAtividadeResponse {

    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    private String titulo;
    private String mensagem;
    private String tipo;
    private String recurso;
    private Long recursoId;
    private LocalDateTime dataCriacao;
}
