package com.empresa.acesso.controller;

import com.empresa.acesso.dto.ModuloResponse;
import com.empresa.acesso.service.ModuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/modulos")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Módulos", description = "Consulta de módulos disponíveis")
public class ModuloController {
    
    private final ModuloService moduloService;
    
    @GetMapping
    @Operation(summary = "Listar módulos", description = "Lista todos os módulos disponíveis")
    public ResponseEntity<List<ModuloResponse>> listarModulos() {
        return ResponseEntity.ok(moduloService.listarModulosDisponiveis());
    }
}
