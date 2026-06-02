package com.wise.sistema_gestao_consultas_backend.controller;

import com.wise.sistema_gestao_consultas_backend.dto.request.CancelarConsultaRequest;
import com.wise.sistema_gestao_consultas_backend.dto.request.ConsultaRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.ConsultaDashboardResponse;
import com.wise.sistema_gestao_consultas_backend.dto.response.ConsultaResponse;
import com.wise.sistema_gestao_consultas_backend.enums.StatusConsulta;
import com.wise.sistema_gestao_consultas_backend.service.ConsultaService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;

    @GetMapping
    public ResponseEntity<List<ConsultaResponse>> listarTodas() {
        return ResponseEntity.ok(consultaService.listarTodas());
    }

    @GetMapping("/relatorio")
    public ResponseEntity<List<ConsultaResponse>> relatorio(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long dentistaId,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long especialidadeId,
            @RequestParam(required = false) StatusConsulta status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim
    ) {
        return ResponseEntity.ok(consultaService.relatorio(
                pacienteId,
                dentistaId,
                usuarioId,
                especialidadeId,
                status,
                dataInicio,
                dataFim
        ));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ConsultaDashboardResponse> dashboard() {
        return ResponseEntity.ok(consultaService.dashboard());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(consultaService.buscarPorId(id));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ConsultaResponse> cadastrar(@Valid @RequestBody ConsultaRequest request) {
        try {
            ConsultaResponse response = consultaService.cadastrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ConsultaRequest request) {
        try {
            ConsultaResponse response = consultaService.atualizar(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ConsultaResponse> cancelar(@PathVariable Long id, @Valid @RequestBody CancelarConsultaRequest request) {
        try {
            ConsultaResponse response = consultaService.cancelar(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            consultaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }
}
