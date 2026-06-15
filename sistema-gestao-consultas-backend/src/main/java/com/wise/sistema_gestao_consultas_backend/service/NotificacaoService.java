package com.wise.sistema_gestao_consultas_backend.service;

import com.wise.sistema_gestao_consultas_backend.dto.response.NotificacaoResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private static final String TOPICO_NOTIFICACOES = "/topic/notificacoes";

    private final SimpMessagingTemplate messagingTemplate;

    public void notificar(String titulo, String mensagem, String tipo, String recurso, Long recursoId) {
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
}
