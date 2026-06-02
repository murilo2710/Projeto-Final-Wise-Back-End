package com.wise.sistema_gestao_consultas_backend.dto.request;

import com.wise.sistema_gestao_consultas_backend.enums.PerfilUsuario;
import com.wise.sistema_gestao_consultas_backend.validation.CpfValido;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres")
    private String nome;

    @NotBlank(message = "CPF e obrigatorio")
    @CpfValido
    @Size(max = 14, message = "CPF deve ter no maximo 14 caracteres")
    private String cpf;

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    @Size(max = 120, message = "Email deve ter no maximo 120 caracteres")
    private String email;

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 6, message = "Senha deve ter no minimo 6 caracteres")
    private String senha;

    @NotNull(message = "Perfil e obrigatorio")
    private PerfilUsuario perfil;

    private Boolean ativo;
}
