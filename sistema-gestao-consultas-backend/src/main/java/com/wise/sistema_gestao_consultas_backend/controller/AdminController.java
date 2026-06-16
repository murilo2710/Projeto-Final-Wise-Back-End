package com.wise.sistema_gestao_consultas_backend.controller;

import com.wise.sistema_gestao_consultas_backend.dto.response.AdminDashboardResponse;
import com.wise.sistema_gestao_consultas_backend.dto.response.LogAtividadeResponse;
import com.wise.sistema_gestao_consultas_backend.service.AdminService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> dashboard() {
        return ResponseEntity.ok(adminService.dashboard());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<LogAtividadeResponse>> listarLogs(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String recurso,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataFim,
            @RequestParam(required = false) Integer limite
    ) {
        return ResponseEntity.ok(adminService.listarLogs(
                usuarioId,
                tipo,
                recurso,
                dataInicio,
                dataFim,
                limite
        ));
    }
}
