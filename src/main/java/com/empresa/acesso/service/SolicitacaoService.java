package com.empresa.acesso.service;

import com.empresa.acesso.dto.*;
import com.empresa.acesso.entity.Modulo;
import com.empresa.acesso.entity.Solicitacao;
import com.empresa.acesso.entity.Usuario;
import com.empresa.acesso.entity.enums.StatusSolicitacao;
import com.empresa.acesso.exception.BusinessException;
import com.empresa.acesso.exception.ResourceNotFoundException;
import com.empresa.acesso.repository.ModuloRepository;
import com.empresa.acesso.repository.SolicitacaoRepository;
import com.empresa.acesso.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {
    
    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModuloRepository moduloRepository;
    
    private static final int DIAS_EXPIRACAO = 180;
    private static final int DIAS_RENOVACAO = 30;
    private static final int LIMITE_MODULOS_PADRAO = 5;
    private static final int LIMITE_MODULOS_TI = 10;
    
    @Transactional
    public CriarSolicitacaoResponse criarSolicitacao(CriarSolicitacaoRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        Set<Modulo> modulos = new HashSet<>(moduloRepository.findAllById(request.getModuloIds()));
        
        if (modulos.size() != request.getModuloIds().size()) {
            throw new BusinessException("Um ou mais módulos não foram encontrados");
        }
        
        validarModulosAtivos(modulos);
        validarSolicitacoesAtivas(usuario.getId(), modulos);
        validarAcessosExistentes(usuario, modulos);
        
        String protocolo = gerarProtocolo();
        
        Solicitacao solicitacao = Solicitacao.builder()
            .protocolo(protocolo)
            .usuario(usuario)
            .modulos(modulos)
            .justificativa(request.getJustificativa())
            .urgente(request.getUrgente())
            .dataSolicitacao(LocalDateTime.now())
            .build();
        
        String motivoNegacao = validarRegrasNegocio(usuario, modulos);
        
        if (motivoNegacao != null) {
            solicitacao.setStatus(StatusSolicitacao.NEGADO);
            solicitacao.setMotivoNegacao(motivoNegacao);
            solicitacao.adicionarHistorico("Solicitação negada: " + motivoNegacao);
            solicitacaoRepository.save(solicitacao);
            
            return CriarSolicitacaoResponse.builder()
                .mensagem("Solicitação negada. Motivo: " + motivoNegacao)
                .protocolo(protocolo)
                .solicitacaoId(solicitacao.getId())
                .build();
        }
        
        solicitacao.setStatus(StatusSolicitacao.ATIVO);
        solicitacao.setDataExpiracao(LocalDateTime.now().plusDays(DIAS_EXPIRACAO));
        solicitacao.adicionarHistorico("Solicitação aprovada automaticamente");
        
        usuario.getModulosAtivos().addAll(modulos);
        usuarioRepository.save(usuario);
        solicitacaoRepository.save(solicitacao);
        
        return CriarSolicitacaoResponse.builder()
            .mensagem("Solicitação criada com sucesso! Protocolo: " + protocolo + ". Seus acessos já estão disponíveis!")
            .protocolo(protocolo)
            .solicitacaoId(solicitacao.getId())
            .build();
    }
    
    private void validarModulosAtivos(Set<Modulo> modulos) {
        for (Modulo modulo : modulos) {
            if (!modulo.getAtivo()) {
                throw new BusinessException("Módulo '" + modulo.getNome() + "' não está ativo");
            }
        }
    }
    
    private void validarSolicitacoesAtivas(Long usuarioId, Set<Modulo> modulos) {
        for (Modulo modulo : modulos) {
            if (solicitacaoRepository.existsSolicitacaoAtivaParaModulo(usuarioId, modulo.getId())) {
                throw new BusinessException("Já existe solicitação ativa para o módulo: " + modulo.getNome());
            }
        }
    }
    
    private void validarAcessosExistentes(Usuario usuario, Set<Modulo> modulos) {
        for (Modulo modulo : modulos) {
            if (usuario.getModulosAtivos().contains(modulo)) {
                throw new BusinessException("Usuário já possui acesso ativo ao módulo: " + modulo.getNome());
            }
        }
    }
    
    private String validarRegrasNegocio(Usuario usuario, Set<Modulo> modulos) {
        String motivoCompatibilidade = validarCompatibilidadeDepartamento(usuario, modulos);
        if (motivoCompatibilidade != null) {
            return motivoCompatibilidade;
        }
        
        String motivoIncompatibilidade = validarModulosIncompativeis(usuario, modulos);
        if (motivoIncompatibilidade != null) {
            return motivoIncompatibilidade;
        }
        
        String motivoLimite = validarLimiteModulos(usuario, modulos);
        if (motivoLimite != null) {
            return motivoLimite;
        }
        
        return null;
    }
    
    private String validarCompatibilidadeDepartamento(Usuario usuario, Set<Modulo> modulos) {
        String departamento = usuario.getDepartamento();
        
        for (Modulo modulo : modulos) {
            if (!modulo.getDepartamentosPermitidos().contains(departamento)) {
                return "Departamento sem permissão para acessar este módulo";
            }
        }
        
        return null;
    }
    
    private String validarModulosIncompativeis(Usuario usuario, Set<Modulo> modulos) {
        Set<Long> modulosAtivosIds = usuario.getModulosAtivos().stream()
            .map(Modulo::getId)
            .collect(Collectors.toSet());
        
        for (Modulo modulo : modulos) {
            for (Long incompativelId : modulo.getModulosIncompativeis()) {
                if (modulosAtivosIds.contains(incompativelId)) {
                    return "Módulo incompatível com outro módulo já ativo em seu perfil";
                }
            }
        }
        
        for (Modulo modulo1 : modulos) {
            for (Modulo modulo2 : modulos) {
                if (!modulo1.equals(modulo2) && modulo1.getModulosIncompativeis().contains(modulo2.getId())) {
                    return "Módulo incompatível com outro módulo já ativo em seu perfil";
                }
            }
        }
        
        return null;
    }
    
    private String validarLimiteModulos(Usuario usuario, Set<Modulo> modulos) {
        long modulosAtivosCount = usuario.getModulosAtivos().size();
        int limite = "TI".equals(usuario.getDepartamento()) ? LIMITE_MODULOS_TI : LIMITE_MODULOS_PADRAO;
        
        if (modulosAtivosCount + modulos.size() > limite) {
            return "Limite de módulos ativos atingido";
        }
        
        return null;
    }
    
    private String gerarProtocolo() {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = solicitacaoRepository.count() + 1;
        return String.format("SOL-%s-%04d", data, count);
    }
    
    @Transactional(readOnly = true)
    public Page<SolicitacaoResponse> listarSolicitacoes(
            String emailUsuario,
            String texto,
            StatusSolicitacao status,
            Boolean urgente,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            int page,
            int size) {
        
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        Pageable pageable = PageRequest.of(page, size);
        
        String statusStr = status != null ? status.name() : null;
        String dataInicioStr = dataInicio != null ? dataInicio.toString() : null;
        String dataFimStr = dataFim != null ? dataFim.toString() : null;
        
        Page<Solicitacao> solicitacoes = solicitacaoRepository.findByUsuarioIdWithFilters(
            usuario.getId(), texto, statusStr, urgente, dataInicioStr, dataFimStr, pageable
        );
        
        return solicitacoes.map(this::toResponse);
    }
    
    @Transactional(readOnly = true)
    public SolicitacaoDetalheResponse buscarDetalhes(Long id, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        Solicitacao solicitacao = solicitacaoRepository.findByIdAndUsuarioId(id, usuario.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));
        
        return toDetalheResponse(solicitacao);
    }
    
    @Transactional
    public void cancelarSolicitacao(Long id, CancelarSolicitacaoRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        Solicitacao solicitacao = solicitacaoRepository.findByIdAndUsuarioId(id, usuario.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));
        
        if (solicitacao.getStatus() != StatusSolicitacao.ATIVO) {
            throw new BusinessException("Apenas solicitações ativas podem ser canceladas");
        }
        
        solicitacao.setStatus(StatusSolicitacao.CANCELADO);
        solicitacao.setMotivoCancelamento(request.getMotivo());
        solicitacao.adicionarHistorico("Solicitação cancelada: " + request.getMotivo());
        
        usuario.getModulosAtivos().removeAll(solicitacao.getModulos());
        
        usuarioRepository.save(usuario);
        solicitacaoRepository.save(solicitacao);
    }
    
    @Transactional
    public CriarSolicitacaoResponse renovarAcesso(Long id, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        Solicitacao solicitacaoOrigem = solicitacaoRepository.findByIdAndUsuarioId(id, usuario.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Solicitação não encontrada"));
        
        if (solicitacaoOrigem.getStatus() != StatusSolicitacao.ATIVO) {
            throw new BusinessException("Apenas solicitações ativas podem ser renovadas");
        }
        
        if (solicitacaoOrigem.getDataExpiracao() == null) {
            throw new BusinessException("Solicitação não possui data de expiração");
        }
        
        LocalDateTime dataLimiteRenovacao = solicitacaoOrigem.getDataExpiracao().minusDays(DIAS_RENOVACAO);
        if (LocalDateTime.now().isBefore(dataLimiteRenovacao)) {
            throw new BusinessException("Renovação disponível apenas 30 dias antes da expiração");
        }
        
        usuario.getModulosAtivos().removeAll(solicitacaoOrigem.getModulos());
        usuarioRepository.save(usuario);
        
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
            .moduloIds(solicitacaoOrigem.getModulos().stream().map(Modulo::getId).collect(Collectors.toSet()))
            .justificativa(solicitacaoOrigem.getJustificativa())
            .urgente(false)
            .build();
        
        CriarSolicitacaoResponse response = criarSolicitacao(request, emailUsuario);
        
        Solicitacao novaSolicitacao = solicitacaoRepository.findById(response.getSolicitacaoId())
            .orElseThrow(() -> new ResourceNotFoundException("Nova solicitação não encontrada"));
        novaSolicitacao.setSolicitacaoOrigem(solicitacaoOrigem);
        solicitacaoRepository.save(novaSolicitacao);
        
        return response;
    }
    
    private SolicitacaoResponse toResponse(Solicitacao solicitacao) {
        return SolicitacaoResponse.builder()
            .id(solicitacao.getId())
            .protocolo(solicitacao.getProtocolo())
            .modulos(solicitacao.getModulos().stream()
                .map(m -> ModuloSimpleResponse.builder()
                    .id(m.getId())
                    .nome(m.getNome())
                    .build())
                .collect(Collectors.toList()))
            .status(solicitacao.getStatus())
            .justificativa(solicitacao.getJustificativa())
            .urgente(solicitacao.getUrgente())
            .dataSolicitacao(solicitacao.getDataSolicitacao())
            .dataExpiracao(solicitacao.getDataExpiracao())
            .motivoNegacao(solicitacao.getMotivoNegacao())
            .motivoCancelamento(solicitacao.getMotivoCancelamento())
            .build();
    }
    
    private SolicitacaoDetalheResponse toDetalheResponse(Solicitacao solicitacao) {
        return SolicitacaoDetalheResponse.builder()
            .id(solicitacao.getId())
            .protocolo(solicitacao.getProtocolo())
            .modulos(solicitacao.getModulos().stream()
                .map(m -> ModuloSimpleResponse.builder()
                    .id(m.getId())
                    .nome(m.getNome())
                    .build())
                .collect(Collectors.toList()))
            .status(solicitacao.getStatus())
            .justificativa(solicitacao.getJustificativa())
            .urgente(solicitacao.getUrgente())
            .dataSolicitacao(solicitacao.getDataSolicitacao())
            .dataExpiracao(solicitacao.getDataExpiracao())
            .motivoNegacao(solicitacao.getMotivoNegacao())
            .motivoCancelamento(solicitacao.getMotivoCancelamento())
            .historico(solicitacao.getHistorico().stream()
                .map(h -> HistoricoResponse.builder()
                    .descricao(h.getDescricao())
                    .dataHora(h.getDataHora())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
