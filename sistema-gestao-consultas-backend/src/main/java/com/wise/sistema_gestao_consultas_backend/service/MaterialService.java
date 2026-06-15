package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.MaterialRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.EspecialidadeResponse;
import com.wise.sistema_gestao_consultas_backend.dto.response.MaterialResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Especialidade;
import com.wise.sistema_gestao_consultas_backend.entity.Material;
import com.wise.sistema_gestao_consultas_backend.repository.EspecialidadeRepository;
import com.wise.sistema_gestao_consultas_backend.repository.MaterialRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final NotificacaoService notificacaoService;

    @Transactional(readOnly = true)
    public List<MaterialResponse> listar(Boolean ativo, Boolean baixoEstoque, Long especialidadeId) {
        return materialRepository.buscarComFiltros(ativo, baixoEstoque, especialidadeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MaterialResponse buscarPorId(Long id) {
        Material material = buscarMaterial(id);
        return toResponse(material);
    }

    @Transactional
    public MaterialResponse cadastrar(MaterialRequest request) {
        validarNomeDuplicado(request.getNome(), null);

        Material material = new Material();
        preencherMaterial(material, request);

        Material salvo = materialRepository.save(material);
        notificacaoService.notificar(
                "Material cadastrado",
                "O material " + salvo.getNome() + " foi cadastrado com sucesso",
                "SUCESSO",
                "MATERIAL",
                salvo.getId()
        );
        return buscarPorId(salvo.getId());
    }

    @Transactional
    public MaterialResponse atualizar(Long id, MaterialRequest request) {
        Material material = buscarMaterial(id);

        validarNomeDuplicado(request.getNome(), id);
        preencherMaterial(material, request);

        Material atualizado = materialRepository.save(material);
        notificacaoService.notificar(
                "Material atualizado",
                "O material " + atualizado.getNome() + " foi atualizado com sucesso",
                "INFO",
                "MATERIAL",
                atualizado.getId()
        );
        return buscarPorId(atualizado.getId());
    }

    @Transactional
    public MaterialResponse ativar(Long id) {
        Material material = buscarMaterial(id);
        material.setAtivo(Boolean.TRUE);
        Material atualizado = materialRepository.save(material);
        notificacaoService.notificar(
                "Material ativado",
                "O material " + atualizado.getNome() + " foi ativado com sucesso",
                "SUCESSO",
                "MATERIAL",
                atualizado.getId()
        );
        return toResponse(atualizado);
    }

    @Transactional
    public MaterialResponse inativar(Long id) {
        Material material = buscarMaterial(id);
        material.setAtivo(Boolean.FALSE);
        Material atualizado = materialRepository.save(material);
        notificacaoService.notificar(
                "Material inativado",
                "O material " + atualizado.getNome() + " foi inativado com sucesso",
                "ALERTA",
                "MATERIAL",
                atualizado.getId()
        );
        return toResponse(atualizado);
    }

    private void preencherMaterial(Material material, MaterialRequest request) {
        material.setNome(request.getNome().trim());
        material.setDescricao(request.getDescricao() == null ? null : request.getDescricao().trim());
        material.setUnidadeMedida(request.getUnidadeMedida().trim());
        material.setQuantidadeAtual(request.getQuantidadeAtual());
        material.setQuantidadeMinima(request.getQuantidadeMinima());
        material.setAtivo(request.getAtivo() == null ? Boolean.TRUE : request.getAtivo());
        material.setEspecialidades(buscarEspecialidades(request.getEspecialidadeIds()));
    }

    private Material buscarMaterial(Long id) {
        return materialRepository.findByIdComEspecialidades(id)
                .orElseThrow(() -> new IllegalArgumentException("Material nao encontrado"));
    }

    private void validarNomeDuplicado(String nome, Long idAtual) {
        materialRepository.findByNome(nome.trim()).ifPresent(material -> {
            if (!material.getId().equals(idAtual)) {
                throw new IllegalStateException("Ja existe material com este nome");
            }
        });
    }

    private Set<Especialidade> buscarEspecialidades(List<Long> especialidadeIds) {
        if (especialidadeIds == null || especialidadeIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Especialidade> especialidades = especialidadeRepository.findAllById(especialidadeIds);
        if (especialidades.size() != new HashSet<>(especialidadeIds).size()) {
            throw new IllegalArgumentException("Uma ou mais especialidades nao foram encontradas");
        }

        return new HashSet<>(especialidades);
    }

    private MaterialResponse toResponse(Material material) {
        boolean baixoEstoque = material.getQuantidadeAtual().compareTo(material.getQuantidadeMinima()) <= 0;

        return new MaterialResponse(
                material.getId(),
                material.getNome(),
                material.getDescricao(),
                material.getUnidadeMedida(),
                material.getQuantidadeAtual(),
                material.getQuantidadeMinima(),
                material.getAtivo(),
                baixoEstoque,
                material.getDataCriacao(),
                material.getEspecialidades()
                        .stream()
                        .map(especialidade -> new EspecialidadeResponse(especialidade.getId(), especialidade.getNome()))
                        .sorted((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()))
                        .toList()
        );
    }
}
