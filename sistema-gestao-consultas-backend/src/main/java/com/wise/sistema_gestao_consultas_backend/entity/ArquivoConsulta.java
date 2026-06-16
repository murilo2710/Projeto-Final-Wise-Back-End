package com.wise.sistema_gestao_consultas_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "arquivos_consulta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoConsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consulta consulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 255)
    private String nomeOriginal;

    @Column(nullable = false, unique = true, length = 255)
    private String nomeArmazenado;

    @Column(nullable = false, length = 120)
    private String tipoConteudo;

    @Column(nullable = false)
    private Long tamanho;

    @Column(nullable = false, length = 500)
    private String caminhoArquivo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataUpload;

    @PrePersist
    public void prePersist() {
        dataUpload = LocalDateTime.now();
    }
}
