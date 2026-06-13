package com.wise.sistema_gestao_consultas_backend.controller;

import com.wise.sistema_gestao_consultas_backend.dto.request.MaterialRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.MaterialResponse;
import com.wise.sistema_gestao_consultas_backend.service.MaterialService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/materiais")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping
    public ResponseEntity<List<MaterialResponse>> listar(
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) Boolean baixoEstoque,
            @RequestParam(required = false) Long especialidadeId
    ) {
        return ResponseEntity.ok(materialService.listar(ativo, baixoEstoque, especialidadeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponse> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(materialService.buscarPorId(id));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<MaterialResponse> cadastrar(@Valid @RequestBody MaterialRequest request) {
        try {
            MaterialResponse response = materialService.cadastrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponse> atualizar(@PathVariable Long id, @Valid @RequestBody MaterialRequest request) {
        try {
            return ResponseEntity.ok(materialService.atualizar(id, request));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<MaterialResponse> ativar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(materialService.ativar(id));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<MaterialResponse> inativar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(materialService.inativar(id));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }
}
