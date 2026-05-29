package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.EspecialidadeRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.EspecialidadeResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Especialidade;
import com.wise.sistema_gestao_consultas_backend.repository.EspecialidadeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;

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

        return toResponse(especialidadeRepository.save(especialidade));
    }

    public EspecialidadeResponse atualizar(Long id, EspecialidadeRequest request) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Especialidade nao encontrada"));

        validarNomeDuplicado(request.getNome(), id);
        especialidade.setNome(request.getNome());

        return toResponse(especialidadeRepository.save(especialidade));
    }

    public void deletar(Long id) {
        if (!especialidadeRepository.existsById(id)) {
            throw new IllegalArgumentException("Especialidade nao encontrada");
        }
        especialidadeRepository.deleteById(id);
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
