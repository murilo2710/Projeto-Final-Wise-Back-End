package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.RegisterRequest;
import com.wise.sistema_gestao_consultas_backend.entity.Usuario;
import com.wise.sistema_gestao_consultas_backend.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario buscarUsuarioAtivoPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new IllegalStateException("Usuario inativo");
        }

        return usuario;
    }

    public Usuario atualizarUltimoLogin(Usuario usuario) {
        usuario.setUltimoLogin(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public Usuario cadastrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Ja existe usuario com este email");
        }

        if (usuarioRepository.existsByCpf(request.getCpf())) {
            throw new IllegalStateException("Ja existe usuario com este CPF");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(request.getPerfil());
        usuario.setAtivo(Boolean.TRUE);

        return usuarioRepository.save(usuario);
    }
}
