package com.wise.sistema_gestao_consultas_backend.repository;

import com.wise.sistema_gestao_consultas_backend.entity.Usuario;
import com.wise.sistema_gestao_consultas_backend.enums.PerfilUsuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    long countByAtivo(Boolean ativo);

    long countByPerfil(PerfilUsuario perfil);
}
