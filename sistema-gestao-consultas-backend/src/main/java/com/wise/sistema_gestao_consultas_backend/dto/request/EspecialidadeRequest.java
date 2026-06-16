package com.wise.sistema_gestao_consultas_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EspecialidadeRequest {

    @NotBlank(message = "Nome e obrigatorio")
    @Pattern(regexp = "^[\\p{L}]+(?:[\\p{L}\\s-]*[\\p{L}])?$", message = "Nome deve conter apenas letras e espacos")
    @Size(min = 3, message = "Nome deve ter no minimo 3 caracteres")
    @Size(max = 100, message = "Nome deve ter no maximo 100 caracteres")
    private String nome;
}
