package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.MovimentacaoEstoqueRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.MovimentacaoEstoqueResponse;
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

        return toResponse(movimentacaoEstoqueRepository.save(movimentacao));
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
}
