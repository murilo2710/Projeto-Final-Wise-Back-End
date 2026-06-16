package com.wise.sistema_gestao_consultas_backend.dto.request;

import com.wise.sistema_gestao_consultas_backend.validation.CpfValido;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DentistaRequest {

    @NotBlank(message = "Nome e obrigatorio")
    @Pattern(regexp = "^[\\p{L}]+(?:[\\p{L}\\s'.-]*[\\p{L}])?$", message = "Nome deve conter apenas letras e espacos")
    @Size(min = 3, message = "Nome deve ter no minimo 3 caracteres")
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

    @NotBlank(message = "CRO e obrigatorio")
    @Pattern(regexp = "^[A-Za-z0-9\\-./\\s]{4,20}$", message = "CRO deve conter apenas letras, numeros e separadores validos")
    @Size(max = 20, message = "CRO deve ter no maximo 20 caracteres")
    private String cro;

    private Boolean ativo;

    private List<Long> especialidadeIds;
}
