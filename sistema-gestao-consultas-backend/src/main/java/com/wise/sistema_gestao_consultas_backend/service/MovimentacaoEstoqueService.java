package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.MovimentacaoEstoqueRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.EstoqueDashboardResponse;
import com.wise.sistema_gestao_consultas_backend.dto.response.MaterialResponse;
import com.wise.sistema_gestao_consultas_backend.dto.response.MovimentacaoEstoqueResponse;
import com.wise.sistema_gestao_consultas_backend.dto.response.EspecialidadeResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Material;
import com.wise.sistema_gestao_consultas_backend.entity.MovimentacaoEstoque;
import com.wise.sistema_gestao_consultas_backend.entity.Usuario;
import com.wise.sistema_gestao_consultas_backend.enums.TipoMovimentacaoEstoque;
import com.wise.sistema_gestao_consultas_backend.repository.MaterialRepository;
import com.wise.sistema_gestao_consultas_backend.repository.MovimentacaoEstoqueRepository;
import com.wise.sistema_gestao_consultas_backend.repository.UsuarioRepository;
import com.wise.sistema_gestao_consultas_backend.security.AuthenticatedUserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final MaterialRepository materialRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final NotificacaoService notificacaoService;

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueResponse> listar(
            Long materialId,
            TipoMovimentacaoEstoque tipo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        return movimentacaoEstoqueRepository.buscarComFiltros(materialId, tipo, dataInicio, dataFim)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstoqueDashboardResponse dashboard() {
        List<Material> materiais = materialRepository.buscarComFiltros(null, null, null);
        List<MovimentacaoEstoque> movimentacoes = movimentacaoEstoqueRepository.buscarComFiltros(null, null, null, null);

        List<MaterialResponse> materiaisBaixoEstoque = materiais.stream()
                .filter(this::isBaixoEstoque)
                .map(this::toMaterialResponse)
                .toList();

        List<MovimentacaoEstoqueResponse> ultimasMovimentacoes = movimentacoes.stream()
                .limit(5)
                .map(this::toResponse)
                .toList();

        return new EstoqueDashboardResponse(
                materiais.size(),
                materiais.stream().filter(material -> Boolean.TRUE.equals(material.getAtivo())).count(),
                materiais.stream().filter(material -> !Boolean.TRUE.equals(material.getAtivo())).count(),
                materiaisBaixoEstoque.size(),
                movimentacoes.size(),
                contarPorTipo(movimentacoes, TipoMovimentacaoEstoque.ENTRADA),
                contarPorTipo(movimentacoes, TipoMovimentacaoEstoque.SAIDA),
                contarPorTipo(movimentacoes, TipoMovimentacaoEstoque.AJUSTE),
                materiaisBaixoEstoque,
                ultimasMovimentacoes
        );
    }

    @Transactional
    public MovimentacaoEstoqueResponse movimentar(MovimentacaoEstoqueRequest request) {
        Material material = buscarMaterialAtivo(request.getMaterialId());
        Usuario usuario = buscarUsuarioLogado();

        BigDecimal estoqueAnterior = material.getQuantidadeAtual();
        BigDecimal estoqueAtual = calcularEstoqueAtual(estoqueAnterior, request);

        material.setQuantidadeAtual(estoqueAtual);
        materialRepository.save(material);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setMaterial(material);
        movimentacao.setUsuario(usuario);
        movimentacao.setTipo(request.getTipo());
        movimentacao.setQuantidade(request.getQuantidade());
        movimentacao.setEstoqueAnterior(estoqueAnterior);
        movimentacao.setEstoqueAtual(estoqueAtual);
        movimentacao.setMotivo(request.getMotivo().trim());

        MovimentacaoEstoque movimentacaoSalva = movimentacaoEstoqueRepository.save(movimentacao);
        notificarMovimentacao(material, movimentacaoSalva);

        return toResponse(movimentacaoSalva);
    }

    private void notificarMovimentacao(Material material, MovimentacaoEstoque movimentacao) {
        notificacaoService.notificar(
                "Estoque atualizado",
                "Movimentacao " + movimentacao.getTipo() + " registrada para " + material.getNome(),
                "INFO",
                "MATERIAL",
                material.getId()
        );

        if (isBaixoEstoque(material)) {
            notificacaoService.notificar(
                    "Baixo estoque",
                    "O material " + material.getNome() + " esta abaixo ou igual ao estoque minimo",
                    "ALERTA",
                    "MATERIAL",
                    material.getId()
            );
        }
    }

    private BigDecimal calcularEstoqueAtual(BigDecimal estoqueAnterior, MovimentacaoEstoqueRequest request) {
        TipoMovimentacaoEstoque tipo = request.getTipo();
        BigDecimal quantidade = request.getQuantidade();

        if (TipoMovimentacaoEstoque.AJUSTE.equals(tipo)) {
            return quantidade;
        }

        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Quantidade deve ser maior que zero");
        }

        if (TipoMovimentacaoEstoque.ENTRADA.equals(tipo)) {
            return estoqueAnterior.add(quantidade);
        }

        if (TipoMovimentacaoEstoque.SAIDA.equals(tipo)) {
            if (quantidade.compareTo(estoqueAnterior) > 0) {
                throw new IllegalStateException("Quantidade de saida maior que o estoque atual");
            }
            return estoqueAnterior.subtract(quantidade);
        }

        throw new IllegalStateException("Tipo de movimentacao invalido");
    }

    private Material buscarMaterialAtivo(Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material nao encontrado"));

        if (!Boolean.TRUE.equals(material.getAtivo())) {
            throw new IllegalStateException("Material inativo");
        }

        return material;
    }

    private Usuario buscarUsuarioLogado() {
        Long usuarioId = authenticatedUserService.getCurrentUserId();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new IllegalStateException("Usuario inativo");
        }

        return usuario;
    }

    private MovimentacaoEstoqueResponse toResponse(MovimentacaoEstoque movimentacao) {
        return new MovimentacaoEstoqueResponse(
                movimentacao.getId(),
                movimentacao.getMaterial().getId(),
                movimentacao.getMaterial().getNome(),
                movimentacao.getUsuario().getId(),
                movimentacao.getUsuario().getNome(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getEstoqueAnterior(),
                movimentacao.getEstoqueAtual(),
                movimentacao.getMotivo(),
                movimentacao.getDataMovimentacao()
        );
    }

    private long contarPorTipo(List<MovimentacaoEstoque> movimentacoes, TipoMovimentacaoEstoque tipo) {
        return movimentacoes.stream()
                .filter(movimentacao -> tipo.equals(movimentacao.getTipo()))
                .count();
    }

    private boolean isBaixoEstoque(Material material) {
        return material.getQuantidadeAtual().compareTo(material.getQuantidadeMinima()) <= 0;
    }

    private MaterialResponse toMaterialResponse(Material material) {
        return new MaterialResponse(
                material.getId(),
                material.getNome(),
                material.getDescricao(),
                material.getUnidadeMedida(),
                material.getQuantidadeAtual(),
                material.getQuantidadeMinima(),
                material.getAtivo(),
                isBaixoEstoque(material),
                material.getDataCriacao(),
                material.getEspecialidades()
                        .stream()
                        .map(especialidade -> new EspecialidadeResponse(especialidade.getId(), especialidade.getNome()))
                        .sorted((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()))
                        .toList()
        );
    }
}
