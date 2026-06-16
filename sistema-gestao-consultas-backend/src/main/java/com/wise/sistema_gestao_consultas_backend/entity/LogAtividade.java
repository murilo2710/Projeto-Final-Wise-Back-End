package com.wise.sistema_gestao_consultas_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "logs_atividade")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    @Column(length = 120)
    private String usuarioNome;

    @Column(length = 120)
    private String usuarioEmail;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(nullable = false, length = 60)
    private String recurso;

    private Long recursoId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        dataCriacao = LocalDateTime.now();
    }
}
