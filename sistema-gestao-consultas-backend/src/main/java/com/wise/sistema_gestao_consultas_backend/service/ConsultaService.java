package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.request.CancelarConsultaRequest;
import com.wise.sistema_gestao_consultas_backend.dto.request.ConsultaRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.ConsultaDashboardResponse;
import com.wise.sistema_gestao_consultas_backend.dto.response.ConsultaResponse;
import com.wise.sistema_gestao_consultas_backend.entity.Consulta;
import com.wise.sistema_gestao_consultas_backend.entity.Dentista;
import com.wise.sistema_gestao_consultas_backend.entity.Paciente;
import com.wise.sistema_gestao_consultas_backend.entity.Usuario;
import com.wise.sistema_gestao_consultas_backend.enums.PerfilUsuario;
import com.wise.sistema_gestao_consultas_backend.enums.StatusConsulta;
import com.wise.sistema_gestao_consultas_backend.repository.ConsultaRepository;
import com.wise.sistema_gestao_consultas_backend.repository.DentistaRepository;
import com.wise.sistema_gestao_consultas_backend.repository.PacienteRepository;
import com.wise.sistema_gestao_consultas_backend.repository.UsuarioRepository;
import com.wise.sistema_gestao_consultas_backend.security.AuthenticatedUserService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public List<ConsultaResponse> listarTodas() {
        return listarConsultasComPermissao()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ConsultaResponse> relatorio(
            Long pacienteId,
            Long dentistaId,
            Long usuarioId,
            Long especialidadeId,
            StatusConsulta status,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        Long usuarioFiltro = PerfilUsuario.ADMIN.equals(authenticatedUserService.getCurrentPerfil())
                ? usuarioId
                : authenticatedUserService.getCurrentUserId();

        return consultaRepository.buscarRelatorio(
                        pacienteId,
                        dentistaId,
                        usuarioFiltro,
                        especialidadeId,
                        status,
                        dataInicio,
                        dataFim
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ConsultaDashboardResponse dashboard() {
        List<Consulta> consultas = listarConsultasComPermissao();
        LocalDateTime agora = LocalDateTime.now();

        List<ConsultaResponse> proximasConsultas = consultas.stream()
                .filter(consulta -> StatusConsulta.AGENDADA.equals(consulta.getStatus()))
                .filter(consulta -> !consulta.getDataInicio().isBefore(agora))
                .sorted(Comparator.comparing(Consulta::getDataInicio))
                .limit(5)
                .map(this::toResponse)
                .toList();

        return new ConsultaDashboardResponse(
                consultas.size(),
                contarPorStatus(consultas, StatusConsulta.AGENDADA),
                contarPorStatus(consultas, StatusConsulta.CANCELADA),
                contarPorStatus(consultas, StatusConsulta.REALIZADA),
                consultas.stream().map(consulta -> consulta.getPaciente().getId()).distinct().count(),
                consultas.stream().map(consulta -> consulta.getDentista().getId()).distinct().count(),
                proximasConsultas
        );
    }

    public ConsultaResponse buscarPorId(Long id) {
        Consulta consulta = buscarConsultaComPermissao(id);
        return toResponse(consulta);
    }

    public ConsultaResponse cadastrar(ConsultaRequest request) {
        StatusConsulta status = request.getStatus() == null ? StatusConsulta.AGENDADA : request.getStatus();
        validarRegras(request, status, null);

        Paciente paciente = buscarPaciente(request.getPacienteId());
        Dentista dentista = buscarDentista(request.getDentistaId());
        Usuario usuario = buscarUsuario(authenticatedUserService.getCurrentUserId());

        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setDentista(dentista);
        consulta.setUsuario(usuario);
        consulta.setDescricao(request.getDescricao());
        consulta.setMotivoCancelamento(status == StatusConsulta.CANCELADA ? request.getMotivoCancelamento() : null);
        consulta.setDataInicio(request.getDataInicio());
        consulta.setDataFim(request.getDataFim());
        consulta.setStatus(status);

        Consulta salva = consultaRepository.save(consulta);
        return buscarPorId(salva.getId());
    }

    public ConsultaResponse atualizar(Long id, ConsultaRequest request) {
        Consulta consulta = buscarConsultaComPermissao(id);

        StatusConsulta status = request.getStatus() == null ? consulta.getStatus() : request.getStatus();
        validarRegras(request, status, id);

        Paciente paciente = buscarPaciente(request.getPacienteId());
        Dentista dentista = buscarDentista(request.getDentistaId());

        consulta.setPaciente(paciente);
        consulta.setDentista(dentista);
        consulta.setDescricao(request.getDescricao());
        consulta.setMotivoCancelamento(status == StatusConsulta.CANCELADA ? request.getMotivoCancelamento() : null);
        consulta.setDataInicio(request.getDataInicio());
        consulta.setDataFim(request.getDataFim());
        consulta.setStatus(status);

        Consulta atualizada = consultaRepository.save(consulta);
        return buscarPorId(atualizada.getId());
    }

    public ConsultaResponse cancelar(Long id, CancelarConsultaRequest request) {
        Consulta consulta = buscarConsultaComPermissao(id);

        consulta.setStatus(StatusConsulta.CANCELADA);
        consulta.setMotivoCancelamento(request.getMotivoCancelamento());

        Consulta atualizada = consultaRepository.save(consulta);
        return toResponse(atualizada);
    }

    public void deletar(Long id) {
        Consulta consulta = buscarConsultaComPermissao(id);
        consultaRepository.delete(consulta);
    }

    private void validarRegras(ConsultaRequest request, StatusConsulta status, Long consultaId) {
        if (request.getDataFim().isEqual(request.getDataInicio()) || request.getDataFim().isBefore(request.getDataInicio())) {
            throw new IllegalStateException("Data/hora final deve ser depois da data/hora inicial");
        }

        if (request.getDataInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Nao e permitido agendar consultas no passado");
        }

        if (status == StatusConsulta.CANCELADA) {
            if (request.getMotivoCancelamento() == null || request.getMotivoCancelamento().isBlank()) {
                throw new IllegalStateException("Motivo de cancelamento e obrigatorio");
            }
        }

        if (status != StatusConsulta.CANCELADA) {
            boolean conflito = consultaRepository.existeConflitoHorario(
                    request.getDentistaId(),
                    StatusConsulta.CANCELADA,
                    consultaId,
                    request.getDataInicio(),
                    request.getDataFim()
            );
            if (conflito) {
                throw new IllegalStateException("Conflito de horario para o dentista informado");
            }
        }
    }

    private Paciente buscarPaciente(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente nao encontrado"));
    }

    private Dentista buscarDentista(Long dentistaId) {
        Dentista dentista = dentistaRepository.findById(dentistaId)
                .orElseThrow(() -> new IllegalArgumentException("Dentista nao encontrado"));

        if (!Boolean.TRUE.equals(dentista.getAtivo())) {
            throw new IllegalStateException("Dentista inativo");
        }
        return dentista;
    }

    private Usuario buscarUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new IllegalStateException("Usuario inativo");
        }
        return usuario;
    }

    private List<Consulta> listarConsultasComPermissao() {
        PerfilUsuario perfil = authenticatedUserService.getCurrentPerfil();
        Long usuarioId = authenticatedUserService.getCurrentUserId();

        if (PerfilUsuario.ADMIN.equals(perfil)) {
            return consultaRepository.findAllComRelacionamentos();
        }

        return consultaRepository.findAllComRelacionamentosPorUsuario(usuarioId);
    }

    private long contarPorStatus(List<Consulta> consultas, StatusConsulta status) {
        return consultas.stream()
                .filter(consulta -> status.equals(consulta.getStatus()))
                .count();
    }

    private Consulta buscarConsultaComPermissao(Long id) {
        PerfilUsuario perfil = authenticatedUserService.getCurrentPerfil();
        Long usuarioId = authenticatedUserService.getCurrentUserId();

        if (PerfilUsuario.ADMIN.equals(perfil)) {
            return consultaRepository.findByIdComRelacionamentos(id)
                    .orElseThrow(() -> new IllegalArgumentException("Consulta nao encontrada"));
        }

        return consultaRepository.findByIdComRelacionamentosPorUsuario(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Consulta nao encontrada"));
    }

    private ConsultaResponse toResponse(Consulta consulta) {
        return new ConsultaResponse(
                consulta.getId(),
                consulta.getPaciente().getId(),
                consulta.getDentista().getId(),
                consulta.getUsuario().getId(),
                consulta.getDescricao(),
                consulta.getMotivoCancelamento(),
                consulta.getDataInicio(),
                consulta.getDataFim(),
                consulta.getDataRegistro(),
                consulta.getStatus()
        );
    }
}
