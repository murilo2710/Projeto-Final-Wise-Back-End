package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.LoginRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.LoginResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioService usuarioService;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioService.buscarUsuarioAtivoPorEmail(request.getEmail());

        if (!usuario.getSenha().equals(request.getSenha())) {
            throw new IllegalArgumentException("Email ou senha invalidos");
        }

        Usuario usuarioAtualizado = usuarioService.atualizarUltimoLogin(usuario);

        return new LoginResponse(
                usuarioAtualizado.getId(),
                usuarioAtualizado.getNome(),
                usuarioAtualizado.getEmail(),
                usuarioAtualizado.getPerfil()
        );
    }
}
