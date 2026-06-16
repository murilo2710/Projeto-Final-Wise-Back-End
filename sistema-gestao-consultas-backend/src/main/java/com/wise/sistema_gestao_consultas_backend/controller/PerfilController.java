package com.wise.sistema_gestao_consultas_backend.controller;

import com.wise.sistema_gestao_consultas_backend.dto.response.UsuarioResponse;
import com.wise.sistema_gestao_consultas_backend.security.AuthenticatedUserService;
import com.wise.sistema_gestao_consultas_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioService usuarioService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    public ResponseEntity<UsuarioResponse> buscarPerfilLogado() {
        Long usuarioId = authenticatedUserService.getCurrentUserId();
        return ResponseEntity.ok(usuarioService.buscarRespostaPorId(usuarioId));
    }
}
