package com.wise.sistema_gestao_consultas_backend.controller;

import com.wise.sistema_gestao_consultas_backend.dto.request.EspecialidadeRequest;
import com.wise.sistema_gestao_consultas_backend.dto.response.EspecialidadeResponse;
import com.wise.sistema_gestao_consultas_backend.service.EspecialidadeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/especialidades")
@RequiredArgsConstructor
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

    @GetMapping
    public ResponseEntity<List<EspecialidadeResponse>> listarTodos() {
        return ResponseEntity.ok(especialidadeService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspecialidadeResponse> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(especialidadeService.buscarPorId(id));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<EspecialidadeResponse> cadastrar(@Valid @RequestBody EspecialidadeRequest request) {
        try {
            EspecialidadeResponse response = especialidadeService.cadastrar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EspecialidadeResponse> atualizar(@PathVariable Long id, @Valid @RequestBody EspecialidadeRequest request) {
        try {
            EspecialidadeResponse response = especialidadeService.atualizar(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            especialidadeService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }
}
