package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.RegisterRequest;
import com.wise.sistema_gestao_consultas_backend.dto.request.UsuarioRequest;
import com.wise.sistema_gestao_consultas_backend.dto.request.UsuarioUpdateRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.UsuarioResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Usuario;
import com.wise.sistema_gestao_consultas_backend.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UsuarioResponse buscarRespostaPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
        return toResponse(usuario);
    }

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
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setPerfil(request.getPerfil());
        usuario.setAtivo(Boolean.TRUE);

        return cadastrarInterno(usuario, null);
    }

    public UsuarioResponse cadastrarViaCrud(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(request.getSenha());
        usuario.setPerfil(request.getPerfil());
        usuario.setAtivo(request.getAtivo() == null ? Boolean.TRUE : request.getAtivo());

        Usuario salvo = cadastrarInterno(usuario, null);
        return toResponse(salvo);
    }

    public UsuarioResponse atualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        validarDuplicidade(request.getEmail(), request.getCpf(), id);

        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setEmail(request.getEmail());
        usuario.setPerfil(request.getPerfil());
        usuario.setAtivo(request.getAtivo());
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        return toResponse(atualizado);
    }

    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario nao encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private Usuario cadastrarInterno(Usuario usuario, Long idAtual) {
        validarDuplicidade(usuario.getEmail(), usuario.getCpf(), idAtual);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    private void validarDuplicidade(String email, String cpf, Long idAtual) {
        usuarioRepository.findByEmail(email).ifPresent(u -> {
            if (!u.getId().equals(idAtual)) {
                throw new IllegalStateException("Ja existe usuario com este email");
            }
        });

        usuarioRepository.findByCpf(cpf).ifPresent(u -> {
            if (!u.getId().equals(idAtual)) {
                throw new IllegalStateException("Ja existe usuario com este CPF");
            }
        });
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getAtivo(),
                usuario.getDataCriacao(),
                usuario.getUltimoLogin()
        );
    }
}
