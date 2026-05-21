package com.wise.sistema_gestao_consultas_backend.dto.response;

import com.wise.sistema_gestao_consultas_backend.enums.PerfilUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private PerfilUsuario perfil;
    private Boolean ativo;
}
