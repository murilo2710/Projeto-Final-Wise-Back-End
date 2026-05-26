package com.wise.sistema_gestao_consultas_backend.repository;

import com.wise.sistema_gestao_consultas_backend.entity.Dentista;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DentistaRepository extends JpaRepository<Dentista, Long> {

    Optional<Dentista> findByEmail(String email);

    Optional<Dentista> findByCpf(String cpf);

    Optional<Dentista> findByCro(String cro);
}
