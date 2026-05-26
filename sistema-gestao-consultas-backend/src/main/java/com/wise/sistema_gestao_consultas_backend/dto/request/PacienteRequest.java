package com.wise.sistema_gestao_consultas_backend.dto.request;

import jakarta.validation.constraints.Email;
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
public class PacienteRequest {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 120, message = "Nome deve ter no maximo 120 caracteres")
    private String nome;

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    @Size(max = 120, message = "Email deve ter no maximo 120 caracteres")
    private String email;

    @NotBlank(message = "CPF e obrigatorio")
    @Size(max = 14, message = "CPF deve ter no maximo 14 caracteres")
    private String cpf;

    @Size(max = 20, message = "Telefone deve ter no maximo 20 caracteres")
    private String telefone;
}
