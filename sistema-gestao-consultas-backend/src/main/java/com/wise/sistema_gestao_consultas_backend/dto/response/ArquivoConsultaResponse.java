package com.wise.sistema_gestao_consultas_backend.dto.response;

import java.time.LocalDateTime;

public record ArquivoConsultaResponse(
        Long id,
        Long consultaId,
        Long usuarioId,
        String usuarioNome,
        String nomeOriginal,
        String tipoConteudo,
        Long tamanho,
        LocalDateTime dataUpload
) {
}
