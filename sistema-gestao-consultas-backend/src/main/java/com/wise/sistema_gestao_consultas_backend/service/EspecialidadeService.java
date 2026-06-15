package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.EspecialidadeRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.EspecialidadeResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Especialidade;
import com.wise.sistema_gestao_consultas_backend.repository.DentistaRepository;
import com.wise.sistema_gestao_consultas_backend.repository.EspecialidadeRepository;
import com.wise.sistema_gestao_consultas_backend.repository.MaterialRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;
    private final DentistaRepository dentistaRepository;
    private final MaterialRepository materialRepository;
    private final NotificacaoService notificacaoService;

    public List<EspecialidadeResponse> listarTodos() {
        return especialidadeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EspecialidadeResponse buscarPorId(Long id) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Especialidade nao encontrada"));
        return toResponse(especialidade);
    }

    public EspecialidadeResponse cadastrar(EspecialidadeRequest request) {
        validarNomeDuplicado(request.getNome(), null);

        Especialidade especialidade = new Especialidade();
        especialidade.setNome(request.getNome());

        Especialidade salva = especialidadeRepository.save(especialidade);
        notificacaoService.notificar(
                "Especialidade cadastrada",
                "A especialidade " + salva.getNome() + " foi cadastrada com sucesso",
                "SUCESSO",
                "ESPECIALIDADE",
                salva.getId()
        );
        return toResponse(salva);
    }

    public EspecialidadeResponse atualizar(Long id, EspecialidadeRequest request) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Especialidade nao encontrada"));

        validarNomeDuplicado(request.getNome(), id);
        especialidade.setNome(request.getNome());

        Especialidade atualizada = especialidadeRepository.save(especialidade);
        notificacaoService.notificar(
                "Especialidade atualizada",
                "A especialidade " + atualizada.getNome() + " foi atualizada com sucesso",
                "INFO",
                "ESPECIALIDADE",
                atualizada.getId()
        );
        return toResponse(atualizada);
    }

    public void deletar(Long id) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Especialidade nao encontrada"));
        if (dentistaRepository.existsByEspecialidadesId(id)) {
            throw new IllegalStateException("Especialidade possui dentistas vinculados e nao pode ser excluida");
        }
        if (materialRepository.existsByEspecialidadesId(id)) {
            throw new IllegalStateException("Especialidade possui materiais vinculados e nao pode ser excluida");
        }
        especialidadeRepository.deleteById(id);
        notificacaoService.notificar(
                "Especialidade excluida",
                "A especialidade " + especialidade.getNome() + " foi excluida com sucesso",
                "ALERTA",
                "ESPECIALIDADE",
                id
        );
    }

    private void validarNomeDuplicado(String nome, Long idAtual) {
        especialidadeRepository.findByNome(nome).ifPresent(e -> {
            if (!e.getId().equals(idAtual)) {
                throw new IllegalStateException("Ja existe especialidade com este nome");
            }
        });
    }

    private EspecialidadeResponse toResponse(Especialidade especialidade) {
        return new EspecialidadeResponse(especialidade.getId(), especialidade.getNome());
    }
}
