package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.entity.RefreshToken;
import com.wise.sistema_gestao_consultas_backend.entity.Usuario;
import com.wise.sistema_gestao_consultas_backend.repository.RefreshTokenRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${security.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    @Transactional
    public String criarParaUsuario(Usuario usuario) {
        refreshTokenRepository.deleteByUsuarioId(usuario.getId());

        String token = gerarTokenSeguro();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setTokenHash(gerarHash(token));
        refreshToken.setDataExpiracao(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000));
        refreshToken.setRevogado(false);

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Transactional
    public Usuario validarERotacionar(String token) {
        RefreshToken refreshToken = buscarTokenAtivo(token);
        Usuario usuario = refreshToken.getUsuario();

        refreshToken.setRevogado(true);
        refreshTokenRepository.save(refreshToken);

        return usuario;
    }

    @Transactional
    public void revogar(String token) {
        refreshTokenRepository.findByTokenHash(gerarHash(token))
                .ifPresent(refreshToken -> {
                    refreshToken.setRevogado(true);
                    refreshTokenRepository.save(refreshToken);
                });
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    private RefreshToken buscarTokenAtivo(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(gerarHash(token))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token invalido"));

        if (Boolean.TRUE.equals(refreshToken.getRevogado())) {
            throw new IllegalArgumentException("Refresh token revogado");
        }

        if (refreshToken.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expirado");
        }

        if (!Boolean.TRUE.equals(refreshToken.getUsuario().getAtivo())) {
            throw new IllegalStateException("Usuario inativo");
        }

        return refreshToken;
    }

    private String gerarTokenSeguro() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String gerarHash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Nao foi possivel gerar hash do refresh token");
        }
    }
}
