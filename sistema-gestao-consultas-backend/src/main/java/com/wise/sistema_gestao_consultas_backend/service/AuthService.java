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
    private final RefreshTokenService refreshTokenService;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioService.buscarUsuarioAtivoPorEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Email ou senha invalidos");
        }

        Usuario usuarioAtualizado = usuarioService.atualizarUltimoLogin(usuario);
        return gerarRespostaAutenticacao(usuarioAtualizado);
    }

    public LoginResponse refresh(String refreshToken) {
        Usuario usuario = refreshTokenService.validarERotacionar(refreshToken);
        return gerarRespostaAutenticacao(usuario);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revogar(refreshToken);
    }

    private LoginResponse gerarRespostaAutenticacao(Usuario usuario) {
        CustomUserDetails userDetails = new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getAtivo(),
                usuario.getPerfil()
        );

        String accessToken = jwtService.gerarToken(userDetails);
        String refreshToken = refreshTokenService.criarParaUsuario(usuario);

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                accessToken,
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getExpirationMs(),
                refreshTokenService.getRefreshExpirationMs()
        );
    }
}
