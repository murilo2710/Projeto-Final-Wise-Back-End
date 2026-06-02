package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.LoginRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.LoginResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Usuario;
import com.wise.sistema_gestao_consultas_backend.security.CustomUserDetails;
import com.wise.sistema_gestao_consultas_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioService.buscarUsuarioAtivoPorEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Email ou senha invalidos");
        }

        Usuario usuarioAtualizado = usuarioService.atualizarUltimoLogin(usuario);
        CustomUserDetails userDetails = new CustomUserDetails(
                usuarioAtualizado.getId(),
                usuarioAtualizado.getEmail(),
                usuarioAtualizado.getSenha(),
                usuarioAtualizado.getAtivo(),
                usuarioAtualizado.getPerfil()
        );
        String token = jwtService.gerarToken(userDetails);

        return new LoginResponse(
                usuarioAtualizado.getId(),
                usuarioAtualizado.getNome(),
                usuarioAtualizado.getEmail(),
                usuarioAtualizado.getPerfil(),
                token,
                "Bearer"
        );
    }
}
