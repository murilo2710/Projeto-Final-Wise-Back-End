package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.response.NotificacaoResponse;
import com.wise.sistema_gestao_consultas_backend.entity.LogAtividade;
import com.wise.sistema_gestao_consultas_backend.entity.Usuario;
import com.wise.sistema_gestao_consultas_backend.repository.LogAtividadeRepository;
import com.wise.sistema_gestao_consultas_backend.repository.UsuarioRepository;
import com.wise.sistema_gestao_consultas_backend.security.CustomUserDetails;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private static final String TOPICO_NOTIFICACOES = "/topic/notificacoes";

    private final SimpMessagingTemplate messagingTemplate;
    private final LogAtividadeRepository logAtividadeRepository;
    private final UsuarioRepository usuarioRepository;

    public void notificar(String titulo, String mensagem, String tipo, String recurso, Long recursoId) {
        registrarLog(titulo, mensagem, tipo, recurso, recursoId);

        NotificacaoResponse notificacao = new NotificacaoResponse(
                titulo,
                mensagem,
                tipo,
                recurso,
                recursoId,
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend(TOPICO_NOTIFICACOES, notificacao);
    }

    private void registrarLog(String titulo, String mensagem, String tipo, String recurso, Long recursoId) {
        Usuario usuario = buscarUsuarioAutenticado();

        LogAtividade log = new LogAtividade();
        log.setUsuarioId(usuario == null ? null : usuario.getId());
        log.setUsuarioNome(usuario == null ? "Sistema" : usuario.getNome());
        log.setUsuarioEmail(usuario == null ? null : usuario.getEmail());
        log.setTitulo(titulo);
        log.setMensagem(mensagem);
        log.setTipo(tipo);
        log.setRecurso(recurso);
        log.setRecursoId(recursoId);

        logAtividadeRepository.save(log);
    }

    private Usuario buscarUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return null;
        }

        return usuarioRepository.findById(userDetails.getId()).orElse(null);
    }
}
