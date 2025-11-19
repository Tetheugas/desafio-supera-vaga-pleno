package com.empresa.acesso.controller;

import com.empresa.acesso.dto.*;
import com.empresa.acesso.entity.enums.StatusSolicitacao;
import com.empresa.acesso.service.SolicitacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Solicitações", description = "Gerenciamento de solicitações de acesso a módulos")
public class SolicitacaoController {
    
    private final SolicitacaoService solicitacaoService;
    
    @PostMapping
    @Operation(summary = "Criar solicitação", description = "Cria uma nova solicitação de acesso a módulos")
    public ResponseEntity<CriarSolicitacaoResponse> criarSolicitacao(
            @Valid @RequestBody CriarSolicitacaoRequest request,
            Authentication authentication) {
        CriarSolicitacaoResponse response = solicitacaoService.criarSolicitacao(
            request, authentication.getName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "Listar solicitações", description = "Lista solicitações do usuário com filtros")
    public ResponseEntity<Page<SolicitacaoResponse>> listarSolicitacoes(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) StatusSolicitacao status,
            @RequestParam(required = false) Boolean urgente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Page<SolicitacaoResponse> response = solicitacaoService.listarSolicitacoes(
            authentication.getName(), texto, status, urgente, dataInicio, dataFim, page, size
        );
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar detalhes", description = "Busca detalhes completos de uma solicitação")
    public ResponseEntity<SolicitacaoDetalheResponse> buscarDetalhes(
            @PathVariable Long id,
            Authentication authentication) {
        SolicitacaoDetalheResponse response = solicitacaoService.buscarDetalhes(id, authentication.getName());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar solicitação", description = "Cancela uma solicitação ativa")
    public ResponseEntity<Void> cancelarSolicitacao(
            @PathVariable Long id,
            @Valid @RequestBody CancelarSolicitacaoRequest request,
            Authentication authentication) {
        solicitacaoService.cancelarSolicitacao(id, request, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/renovar")
    @Operation(summary = "Renovar acesso", description = "Renova acesso a módulos de uma solicitação")
    public ResponseEntity<CriarSolicitacaoResponse> renovarAcesso(
            @PathVariable Long id,
            Authentication authentication) {
        CriarSolicitacaoResponse response = solicitacaoService.renovarAcesso(id, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
