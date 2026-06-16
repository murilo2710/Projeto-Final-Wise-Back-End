package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.response.AdminDashboardResponse;
import com.wise.sistema_gestao_consultas_backend.dto.response.LogAtividadeResponse;
import com.wise.sistema_gestao_consultas_backend.entity.LogAtividade;
import com.wise.sistema_gestao_consultas_backend.enums.PerfilUsuario;
import com.wise.sistema_gestao_consultas_backend.enums.StatusConsulta;
import com.wise.sistema_gestao_consultas_backend.repository.ConsultaRepository;
import com.wise.sistema_gestao_consultas_backend.repository.DentistaRepository;
import com.wise.sistema_gestao_consultas_backend.repository.LogAtividadeRepository;
import com.wise.sistema_gestao_consultas_backend.repository.MaterialRepository;
import com.wise.sistema_gestao_consultas_backend.repository.MovimentacaoEstoqueRepository;
import com.wise.sistema_gestao_consultas_backend.repository.PacienteRepository;
import com.wise.sistema_gestao_consultas_backend.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final DentistaRepository dentistaRepository;
    private final ConsultaRepository consultaRepository;
    private final MaterialRepository materialRepository;
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final LogAtividadeRepository logAtividadeRepository;

    @Transactional(readOnly = true)
    public AdminDashboardResponse dashboard() {
        return new AdminDashboardResponse(
                usuarioRepository.count(),
                usuarioRepository.countByAtivo(Boolean.TRUE),
                usuarioRepository.countByAtivo(Boolean.FALSE),
                usuarioRepository.countByPerfil(PerfilUsuario.ADMIN),
                usuarioRepository.countByPerfil(PerfilUsuario.DENTISTA),
                pacienteRepository.count(),
                dentistaRepository.count(),
                dentistaRepository.countByAtivo(Boolean.TRUE),
                dentistaRepository.countByAtivo(Boolean.FALSE),
                consultaRepository.count(),
                consultaRepository.countByStatus(StatusConsulta.AGENDADA),
                consultaRepository.countByStatus(StatusConsulta.FINALIZADA),
                consultaRepository.countByStatus(StatusConsulta.CANCELADA),
                materialRepository.count(),
                materialRepository.countBaixoEstoque(),
                movimentacaoEstoqueRepository.count(),
                logAtividadeRepository.count(),
                logAtividadeRepository.findTop10ByOrderByDataCriacaoDesc()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public List<LogAtividadeResponse> listarLogs(
            Long usuarioId,
            String tipo,
            String recurso,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Integer limite
    ) {
        int limiteSeguro = limite == null ? 50 : Math.max(1, Math.min(limite, 200));

        return logAtividadeRepository.buscarComFiltros(
                        usuarioId,
                        normalizarFiltro(tipo),
                        normalizarFiltro(recurso),
                        dataInicio,
                        dataFim,
                        PageRequest.of(0, limiteSeguro)
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private String normalizarFiltro(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim().toUpperCase();
    }

    private LogAtividadeResponse toResponse(LogAtividade log) {
        return new LogAtividadeResponse(
                log.getId(),
                log.getUsuarioId(),
                log.getUsuarioNome(),
                log.getUsuarioEmail(),
                log.getTitulo(),
                log.getMensagem(),
                log.getTipo(),
                log.getRecurso(),
                log.getRecursoId(),
                log.getDataCriacao()
        );
    }
}
