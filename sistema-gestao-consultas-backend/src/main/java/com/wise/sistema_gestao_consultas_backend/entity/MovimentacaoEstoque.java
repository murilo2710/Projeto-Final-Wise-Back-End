package com.wise.sistema_gestao_consultas_backend.entity;

import com.wise.sistema_gestao_consultas_backend.enums.TipoMovimentacaoEstoque;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movimentacoes_estoque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_material", nullable = false)
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMovimentacaoEstoque tipo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estoqueAnterior;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estoqueAtual;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataMovimentacao;

    @PrePersist
    public void prePersist() {
        dataMovimentacao = LocalDateTime.now();
    }
}
