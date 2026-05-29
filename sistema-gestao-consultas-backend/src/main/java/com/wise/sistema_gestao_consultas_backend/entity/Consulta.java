package com.wise.sistema_gestao_consultas_backend.entity;

import com.wise.sistema_gestao_consultas_backend.enums.StatusConsulta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "consultas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentista_id", nullable = false)
    private Dentista dentista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "id_paciente", nullable = false)
    private Long idPacienteLegado;

    @Column(name = "id_dentista", nullable = false)
    private Long idDentistaLegado;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuarioLegado;

    @Column(length = 500)
    private String descricao;

    @Column(length = 500)
    private String motivoCancelamento;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConsulta status = StatusConsulta.AGENDADA;

    @PrePersist
    public void prePersist() {
        sincronizarChavesLegadas();
        dataRegistro = LocalDateTime.now();
    }

    @jakarta.persistence.PreUpdate
    public void preUpdate() {
        sincronizarChavesLegadas();
    }

    private void sincronizarChavesLegadas() {
        if (paciente != null) {
            idPacienteLegado = paciente.getId();
        }
        if (dentista != null) {
            idDentistaLegado = dentista.getId();
        }
        if (usuario != null) {
            idUsuarioLegado = usuario.getId();
        }
    }
}
