package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.DentistaRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.DentistaResponse;
import com.wise.sistema_gestao_consultas_backend.dto.response.EspecialidadeResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Dentista;
import com.wise.sistema_gestao_consultas_backend.entity.Especialidade;
import com.wise.sistema_gestao_consultas_backend.repository.ConsultaRepository;
import com.wise.sistema_gestao_consultas_backend.repository.DentistaRepository;
import com.wise.sistema_gestao_consultas_backend.repository.EspecialidadeRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DentistaService {

    private final DentistaRepository dentistaRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final ConsultaRepository consultaRepository;
    private final NotificacaoService notificacaoService;

    @Transactional(readOnly = true)
    public List<DentistaResponse> listarTodos() {
        return dentistaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DentistaResponse buscarPorId(Long id) {
        Dentista dentista = dentistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dentista nao encontrado"));
        return toResponse(dentista);
    }

    @Transactional
    public DentistaResponse cadastrar(DentistaRequest request) {
        validarDuplicidade(request.getEmail(), request.getCpf(), request.getCro(), null);

        Dentista dentista = new Dentista();
        dentista.setNome(request.getNome());
        dentista.setCpf(request.getCpf());
        dentista.setEmail(request.getEmail());
        dentista.setCro(request.getCro());
        dentista.setAtivo(request.getAtivo() == null ? Boolean.TRUE : request.getAtivo());
        dentista.setEspecialidades(buscarEspecialidades(request.getEspecialidadeIds()));

        Dentista salvo = dentistaRepository.save(dentista);
        notificacaoService.notificar(
                "Dentista cadastrado",
                "O dentista " + salvo.getNome() + " foi cadastrado com sucesso",
                "SUCESSO",
                "DENTISTA",
                salvo.getId()
        );
        return toResponse(salvo);
    }

    @Transactional
    public DentistaResponse atualizar(Long id, DentistaRequest request) {
        Dentista dentista = dentistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dentista nao encontrado"));

        validarDuplicidade(request.getEmail(), request.getCpf(), request.getCro(), id);

        dentista.setNome(request.getNome());
        dentista.setCpf(request.getCpf());
        dentista.setEmail(request.getEmail());
        dentista.setCro(request.getCro());
        if (request.getAtivo() != null) {
            dentista.setAtivo(request.getAtivo());
        }
        if (request.getEspecialidadeIds() != null) {
            dentista.setEspecialidades(buscarEspecialidades(request.getEspecialidadeIds()));
        }

        Dentista atualizado = dentistaRepository.save(dentista);
        notificacaoService.notificar(
                "Dentista atualizado",
                "O dentista " + atualizado.getNome() + " foi atualizado com sucesso",
                "INFO",
                "DENTISTA",
                atualizado.getId()
        );
        return toResponse(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        Dentista dentista = dentistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dentista nao encontrado"));
        if (consultaRepository.existsByDentistaId(id)) {
            throw new IllegalStateException("Dentista possui consultas registradas e nao pode ser excluido");
        }
        dentistaRepository.deleteById(id);
        notificacaoService.notificar(
                "Dentista excluido",
                "O dentista " + dentista.getNome() + " foi excluido com sucesso",
                "ALERTA",
                "DENTISTA",
                id
        );
    }

    private void validarDuplicidade(String email, String cpf, String cro, Long idAtual) {
        if (email != null) {
            dentistaRepository.findByEmail(email).ifPresent(d -> {
                if (!d.getId().equals(idAtual)) {
                    throw new IllegalStateException("Ja existe dentista com este email");
                }
            });
        }

        if (cpf != null) {
            dentistaRepository.findByCpf(cpf).ifPresent(d -> {
                if (!d.getId().equals(idAtual)) {
                    throw new IllegalStateException("Ja existe dentista com este CPF");
                }
            });
        }

        if (cro != null) {
            dentistaRepository.findByCro(cro).ifPresent(d -> {
                if (!d.getId().equals(idAtual)) {
                    throw new IllegalStateException("Ja existe dentista com este CRO");
                }
            });
        }
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

    private DentistaResponse toResponse(Dentista dentista) {
        return new DentistaResponse(
                dentista.getId(),
                dentista.getNome(),
                dentista.getCpf(),
                dentista.getEmail(),
                dentista.getCro(),
                dentista.getAtivo(),
                dentista.getDataCriacao(),
                dentista.getEspecialidades()
                        .stream()
                        .map(especialidade -> new EspecialidadeResponse(especialidade.getId(), especialidade.getNome()))
                        .sorted((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()))
                        .toList()
        );
    }
}
