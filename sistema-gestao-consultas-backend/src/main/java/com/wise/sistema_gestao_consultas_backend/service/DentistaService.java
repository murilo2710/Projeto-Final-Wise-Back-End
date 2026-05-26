package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.DentistaRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.DentistaResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Dentista;
import com.wise.sistema_gestao_consultas_backend.repository.DentistaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DentistaService {

    private final DentistaRepository dentistaRepository;

    public List<DentistaResponse> listarTodos() {
        return dentistaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public DentistaResponse buscarPorId(Long id) {
        Dentista dentista = dentistaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dentista nao encontrado"));
        return toResponse(dentista);
    }

    public DentistaResponse cadastrar(DentistaRequest request) {
        validarDuplicidade(request.getEmail(), request.getCpf(), request.getCro(), null);

        Dentista dentista = new Dentista();
        dentista.setNome(request.getNome());
        dentista.setCpf(request.getCpf());
        dentista.setEmail(request.getEmail());
        dentista.setCro(request.getCro());
        dentista.setAtivo(request.getAtivo() == null ? Boolean.TRUE : request.getAtivo());

        return toResponse(dentistaRepository.save(dentista));
    }

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

        return toResponse(dentistaRepository.save(dentista));
    }

    public void deletar(Long id) {
        if (!dentistaRepository.existsById(id)) {
            throw new IllegalArgumentException("Dentista nao encontrado");
        }
        dentistaRepository.deleteById(id);
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

    private DentistaResponse toResponse(Dentista dentista) {
        return new DentistaResponse(
                dentista.getId(),
                dentista.getNome(),
                dentista.getCpf(),
                dentista.getEmail(),
                dentista.getCro(),
                dentista.getAtivo(),
                dentista.getDataCriacao()
        );
    }
}
