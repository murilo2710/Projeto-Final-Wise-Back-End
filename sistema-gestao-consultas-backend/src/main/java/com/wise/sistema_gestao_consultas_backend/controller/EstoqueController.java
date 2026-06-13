package com.wise.sistema_gestao_consultas_backend.controller;

import com.wise.sistema_gestao_consultas_backend.dto.request.MovimentacaoEstoqueRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.EstoqueDashboardResponse;
import com.wise.sistema_gestao_consultas_backend.dto.response.MovimentacaoEstoqueResponse;
import com.wise.sistema_gestao_consultas_backend.enums.TipoMovimentacaoEstoque;
import com.wise.sistema_gestao_consultas_backend.service.MovimentacaoEstoqueService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    @GetMapping("/dashboard")
    public ResponseEntity<EstoqueDashboardResponse> dashboard() {
        return ResponseEntity.ok(movimentacaoEstoqueService.dashboard());
    }

    @GetMapping("/movimentacoes")
    public ResponseEntity<List<MovimentacaoEstoqueResponse>> listarMovimentacoes(
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) TipoMovimentacaoEstoque tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim
    ) {
        return ResponseEntity.ok(movimentacaoEstoqueService.listar(materialId, tipo, dataInicio, dataFim));
    }

    @PostMapping("/movimentacoes")
    public ResponseEntity<MovimentacaoEstoqueResponse> movimentar(@Valid @RequestBody MovimentacaoEstoqueRequest request) {
        try {
            MovimentacaoEstoqueResponse response = movimentacaoEstoqueService.movimentar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }
}
