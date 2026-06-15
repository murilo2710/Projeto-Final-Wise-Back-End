package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.PacienteRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.PacienteResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Paciente;
import com.wise.sistema_gestao_consultas_backend.repository.ConsultaRepository;
import com.wise.sistema_gestao_consultas_backend.repository.PacienteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final ConsultaRepository consultaRepository;
    private final NotificacaoService notificacaoService;

    public List<PacienteResponse> listarTodos() {
        return pacienteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PacienteResponse buscarPorId(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente nao encontrado"));
        return toResponse(paciente);
    }

    public PacienteResponse cadastrar(PacienteRequest request) {
        validarDuplicidade(request.getEmail(), request.getCpf(), null);

        Paciente paciente = new Paciente();
        paciente.setNome(request.getNome());
        paciente.setEmail(request.getEmail());
        paciente.setCpf(request.getCpf());
        paciente.setTelefone(request.getTelefone());

        Paciente salvo = pacienteRepository.save(paciente);
        notificacaoService.notificar(
                "Paciente cadastrado",
                "O paciente " + salvo.getNome() + " foi cadastrado com sucesso",
                "SUCESSO",
                "PACIENTE",
                salvo.getId()
        );
        return toResponse(salvo);
    }

    public PacienteResponse atualizar(Long id, PacienteRequest request) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente nao encontrado"));

        validarDuplicidade(request.getEmail(), request.getCpf(), id);

        paciente.setNome(request.getNome());
        paciente.setEmail(request.getEmail());
        paciente.setCpf(request.getCpf());
        paciente.setTelefone(request.getTelefone());

        Paciente atualizado = pacienteRepository.save(paciente);
        notificacaoService.notificar(
                "Paciente atualizado",
                "O paciente " + atualizado.getNome() + " foi atualizado com sucesso",
                "INFO",
                "PACIENTE",
                atualizado.getId()
        );
        return toResponse(atualizado);
    }

    public void deletar(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente nao encontrado"));
        if (consultaRepository.existsByPacienteId(id)) {
            throw new IllegalStateException("Paciente possui consultas registradas e nao pode ser excluido");
        }
        pacienteRepository.deleteById(id);
        notificacaoService.notificar(
                "Paciente excluido",
                "O paciente " + paciente.getNome() + " foi excluido com sucesso",
                "ALERTA",
                "PACIENTE",
                id
        );
    }

    private void validarDuplicidade(String email, String cpf, Long idAtual) {
        if (email != null) {
            pacienteRepository.findByEmail(email).ifPresent(p -> {
                if (!p.getId().equals(idAtual)) {
                    throw new IllegalStateException("Ja existe paciente com este email");
                }
            });
        }

        if (cpf != null) {
            pacienteRepository.findByCpf(cpf).ifPresent(p -> {
                if (!p.getId().equals(idAtual)) {
                    throw new IllegalStateException("Ja existe paciente com este CPF");
                }
            });
        }
    }

    private PacienteResponse toResponse(Paciente paciente) {
        return new PacienteResponse(
                paciente.getId(),
                paciente.getNome(),
                paciente.getEmail(),
                paciente.getCpf(),
                paciente.getTelefone(),
                paciente.getDataCriacao()
        );
    }
}
