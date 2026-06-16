package com.wise.sistema_gestao_consultas_backend.controller;

import com.wise.sistema_gestao_consultas_backend.dto.response.ArquivoConsultaResponse;
import com.wise.sistema_gestao_consultas_backend.service.ArquivoConsultaService;
import com.wise.sistema_gestao_consultas_backend.service.ArquivoConsultaService.ArquivoDownload;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
public class ArquivoConsultaController {

    private final ArquivoConsultaService arquivoConsultaService;

    @PostMapping(value = "/{consultaId}/arquivos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArquivoConsultaResponse> anexar(
            @PathVariable Long consultaId,
            @RequestParam("arquivo") MultipartFile arquivo
    ) {
        ArquivoConsultaResponse response = arquivoConsultaService.anexar(consultaId, arquivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{consultaId}/arquivos")
    public ResponseEntity<List<ArquivoConsultaResponse>> listarPorConsulta(@PathVariable Long consultaId) {
        return ResponseEntity.ok(arquivoConsultaService.listarPorConsulta(consultaId));
    }

    @GetMapping("/arquivos/{arquivoId}/download")
    public ResponseEntity<?> download(@PathVariable Long arquivoId) {
        ArquivoDownload arquivo = arquivoConsultaService.carregarParaDownload(arquivoId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(arquivo.tipoConteudo()))
                .contentLength(arquivo.tamanho())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(arquivo.nomeOriginal(), StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(arquivo.resource());
    }

    @DeleteMapping("/arquivos/{arquivoId}")
    public ResponseEntity<Void> deletar(@PathVariable Long arquivoId) {
        arquivoConsultaService.deletar(arquivoId);
        return ResponseEntity.noContent().build();
    }
}
