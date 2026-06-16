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
public class LoginResponse {

    private Long id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;
    private String token;
    private String accessToken;
    private String refreshToken;
    private String tipoToken;
    private Long expiresInMs;
    private Long refreshExpiresInMs;
}
