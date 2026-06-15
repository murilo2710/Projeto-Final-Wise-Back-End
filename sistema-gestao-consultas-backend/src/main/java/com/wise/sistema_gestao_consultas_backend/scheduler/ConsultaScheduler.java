package com.wise.sistema_gestao_consultas_backend.scheduler;

import com.wise.sistema_gestao_consultas_backend.service.ConsultaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsultaScheduler {

    private final ConsultaService consultaService;

    @Scheduled(
            fixedDelayString = "${app.consultas.finalizacao-automatica-ms:60000}",
            initialDelayString = "${app.consultas.finalizacao-initial-delay-ms:30000}"
    )
    public void finalizarConsultasAgendadasVencidas() {
        int totalFinalizadas = consultaService.finalizarConsultasAgendadasVencidas();

        if (totalFinalizadas > 0) {
            log.info("{} consulta(s) agendada(s) vencida(s) foram finalizada(s) automaticamente", totalFinalizadas);
        }
    }
}
