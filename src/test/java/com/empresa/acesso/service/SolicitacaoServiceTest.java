package com.empresa.acesso.service;

import com.empresa.acesso.dto.CancelarSolicitacaoRequest;
import com.empresa.acesso.dto.CriarSolicitacaoRequest;
import com.empresa.acesso.dto.CriarSolicitacaoResponse;
import com.empresa.acesso.entity.Modulo;
import com.empresa.acesso.entity.Solicitacao;
import com.empresa.acesso.entity.Usuario;
import com.empresa.acesso.entity.enums.StatusSolicitacao;
import com.empresa.acesso.exception.BusinessException;
import com.empresa.acesso.exception.ResourceNotFoundException;
import com.empresa.acesso.repository.ModuloRepository;
import com.empresa.acesso.repository.SolicitacaoRepository;
import com.empresa.acesso.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitacaoServiceTest {
    
    @Mock
    private SolicitacaoRepository solicitacaoRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private ModuloRepository moduloRepository;
    
    @InjectMocks
    private SolicitacaoService solicitacaoService;
    
    private Usuario usuario;
    private Modulo modulo1;
    private Modulo modulo2;
    
    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
            .id(1L)
            .email("teste@empresa.com")
            .nome("Teste")
            .departamento("TI")
            .ativo(true)
            .modulosAtivos(new HashSet<>())
            .build();
        
        modulo1 = Modulo.builder()
            .id(1L)
            .nome("Portal")
            .descricao("Portal do Colaborador")
            .ativo(true)
            .departamentosPermitidos(Set.of("TI", "Financeiro", "RH", "Operações"))
            .modulosIncompativeis(new HashSet<>())
            .build();
        
        modulo2 = Modulo.builder()
            .id(2L)
            .nome("Relatórios")
            .descricao("Relatórios Gerenciais")
            .ativo(true)
            .departamentosPermitidos(Set.of("TI", "Financeiro", "RH", "Operações"))
            .modulosIncompativeis(new HashSet<>())
            .build();
    }
    
    @Test
    void deveCriarSolicitacaoComSucesso() {
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
            .moduloIds(Set.of(1L, 2L))
            .justificativa("Preciso acessar estes módulos para realizar minhas atividades diárias")
            .urgente(false)
            .build();
        
        when(usuarioRepository.findByEmail(eq("teste@empresa.com"))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllById(eq(Set.of(1L, 2L)))).thenReturn(List.of(modulo1, modulo2));
        when(solicitacaoRepository.existsSolicitacaoAtivaParaModulo(eq(1L), eq(1L))).thenReturn(false);
        when(solicitacaoRepository.existsSolicitacaoAtivaParaModulo(eq(1L), eq(2L))).thenReturn(false);
        when(solicitacaoRepository.count()).thenReturn(0L);
        when(usuarioRepository.save(eq(usuario))).thenReturn(usuario);
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenAnswer(invocation -> {
            Solicitacao s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });
        
        CriarSolicitacaoResponse response = solicitacaoService.criarSolicitacao(request, "teste@empresa.com");
        
        assertNotNull(response);
        assertTrue(response.getMensagem().contains("Solicitação criada com sucesso"));
        assertNotNull(response.getProtocolo());
        
        verify(usuarioRepository).findByEmail(eq("teste@empresa.com"));
        verify(moduloRepository).findAllById(eq(Set.of(1L, 2L)));
        verify(usuarioRepository).save(eq(usuario));
        verify(solicitacaoRepository).save(any(Solicitacao.class));
    }
    
    @Test
    void deveNegarSolicitacaoPorDepartamentoIncompativel() {
        Usuario usuarioFinanceiro = Usuario.builder()
            .id(2L)
            .email("financeiro@empresa.com")
            .nome("Financeiro")
            .departamento("Financeiro")
            .ativo(true)
            .modulosAtivos(new HashSet<>())
            .build();
        
        Modulo moduloAuditoria = Modulo.builder()
            .id(10L)
            .nome("Auditoria")
            .descricao("Sistema de Auditoria")
            .ativo(true)
            .departamentosPermitidos(Set.of("TI"))
            .modulosIncompativeis(new HashSet<>())
            .build();
        
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
            .moduloIds(Set.of(10L))
            .justificativa("Preciso acessar o módulo de auditoria para verificar logs")
            .urgente(false)
            .build();
        
        when(usuarioRepository.findByEmail(eq("financeiro@empresa.com"))).thenReturn(Optional.of(usuarioFinanceiro));
        when(moduloRepository.findAllById(eq(Set.of(10L)))).thenReturn(List.of(moduloAuditoria));
        when(solicitacaoRepository.existsSolicitacaoAtivaParaModulo(eq(2L), eq(10L))).thenReturn(false);
        when(solicitacaoRepository.count()).thenReturn(0L);
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenAnswer(invocation -> {
            Solicitacao s = invocation.getArgument(0);
            s.setId(1L);
            return s;
        });
        
        CriarSolicitacaoResponse response = solicitacaoService.criarSolicitacao(request, "financeiro@empresa.com");
        
        assertNotNull(response);
        assertTrue(response.getMensagem().contains("Solicitação negada"));
        assertTrue(response.getMensagem().contains("Departamento sem permissão"));
        
        verify(solicitacaoRepository).save(any(Solicitacao.class));
    }
    
    @Test
    void deveLancarExcecaoQuandoModuloNaoEncontrado() {
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
            .moduloIds(Set.of(1L, 999L))
            .justificativa("Preciso acessar estes módulos para realizar minhas atividades")
            .urgente(false)
            .build();
        
        when(usuarioRepository.findByEmail(eq("teste@empresa.com"))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllById(eq(Set.of(1L, 999L)))).thenReturn(List.of(modulo1));
        
        assertThrows(BusinessException.class, () -> 
            solicitacaoService.criarSolicitacao(request, "teste@empresa.com")
        );
        
        verify(usuarioRepository).findByEmail(eq("teste@empresa.com"));
        verify(moduloRepository).findAllById(eq(Set.of(1L, 999L)));
    }
    
    @Test
    void deveLancarExcecaoQuandoModuloInativo() {
        modulo1.setAtivo(false);
        
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
            .moduloIds(Set.of(1L))
            .justificativa("Preciso acessar este módulo para realizar minhas atividades")
            .urgente(false)
            .build();
        
        when(usuarioRepository.findByEmail(eq("teste@empresa.com"))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllById(eq(Set.of(1L)))).thenReturn(List.of(modulo1));
        
        assertThrows(BusinessException.class, () -> 
            solicitacaoService.criarSolicitacao(request, "teste@empresa.com")
        );
    }
    
    @Test
    void deveLancarExcecaoQuandoJaExisteSolicitacaoAtiva() {
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
            .moduloIds(Set.of(1L))
            .justificativa("Preciso acessar este módulo para realizar minhas atividades")
            .urgente(false)
            .build();
        
        when(usuarioRepository.findByEmail(eq("teste@empresa.com"))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllById(eq(Set.of(1L)))).thenReturn(List.of(modulo1));
        when(solicitacaoRepository.existsSolicitacaoAtivaParaModulo(eq(1L), eq(1L))).thenReturn(true);
        
        assertThrows(BusinessException.class, () -> 
            solicitacaoService.criarSolicitacao(request, "teste@empresa.com")
        );
    }
    
    @Test
    void deveLancarExcecaoQuandoUsuarioJaPossuiAcesso() {
        usuario.getModulosAtivos().add(modulo1);
        
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
            .moduloIds(Set.of(1L))
            .justificativa("Preciso acessar este módulo para realizar minhas atividades")
            .urgente(false)
            .build();
        
        when(usuarioRepository.findByEmail(eq("teste@empresa.com"))).thenReturn(Optional.of(usuario));
        when(moduloRepository.findAllById(eq(Set.of(1L)))).thenReturn(List.of(modulo1));
        when(solicitacaoRepository.existsSolicitacaoAtivaParaModulo(eq(1L), eq(1L))).thenReturn(false);
        
        assertThrows(BusinessException.class, () -> 
            solicitacaoService.criarSolicitacao(request, "teste@empresa.com")
        );
    }
    
    @Test
    void deveCancelarSolicitacaoComSucesso() {
        Solicitacao solicitacao = Solicitacao.builder()
            .id(1L)
            .protocolo("SOL-20241118-0001")
            .usuario(usuario)
            .modulos(Set.of(modulo1))
            .status(StatusSolicitacao.ATIVO)
            .dataSolicitacao(LocalDateTime.now())
            .build();
        
        usuario.getModulosAtivos().add(modulo1);
        
        CancelarSolicitacaoRequest request = CancelarSolicitacaoRequest.builder()
            .motivo("Não preciso mais deste acesso")
            .build();
        
        when(usuarioRepository.findByEmail(eq("teste@empresa.com"))).thenReturn(Optional.of(usuario));
        when(solicitacaoRepository.findByIdAndUsuarioId(eq(1L), eq(1L))).thenReturn(Optional.of(solicitacao));
        when(usuarioRepository.save(eq(usuario))).thenReturn(usuario);
        when(solicitacaoRepository.save(eq(solicitacao))).thenReturn(solicitacao);
        
        solicitacaoService.cancelarSolicitacao(1L, request, "teste@empresa.com");
        
        assertEquals(StatusSolicitacao.CANCELADO, solicitacao.getStatus());
        assertEquals("Não preciso mais deste acesso", solicitacao.getMotivoCancelamento());
        assertFalse(usuario.getModulosAtivos().contains(modulo1));
        
        verify(usuarioRepository).save(eq(usuario));
        verify(solicitacaoRepository).save(eq(solicitacao));
    }
    
    @Test
    void deveLancarExcecaoAoCancelarSolicitacaoNaoAtiva() {
        Solicitacao solicitacao = Solicitacao.builder()
            .id(1L)
            .protocolo("SOL-20241118-0001")
            .usuario(usuario)
            .modulos(Set.of(modulo1))
            .status(StatusSolicitacao.CANCELADO)
            .dataSolicitacao(LocalDateTime.now())
            .build();
        
        CancelarSolicitacaoRequest request = CancelarSolicitacaoRequest.builder()
            .motivo("Não preciso mais deste acesso")
            .build();
        
        when(usuarioRepository.findByEmail(eq("teste@empresa.com"))).thenReturn(Optional.of(usuario));
        when(solicitacaoRepository.findByIdAndUsuarioId(eq(1L), eq(1L))).thenReturn(Optional.of(solicitacao));
        
        assertThrows(BusinessException.class, () -> 
            solicitacaoService.cancelarSolicitacao(1L, request, "teste@empresa.com")
        );
    }
    
    @Test
    void deveLancarExcecaoQuandoSolicitacaoNaoEncontrada() {
        CancelarSolicitacaoRequest request = CancelarSolicitacaoRequest.builder()
            .motivo("Não preciso mais deste acesso")
            .build();
        
        when(usuarioRepository.findByEmail(eq("teste@empresa.com"))).thenReturn(Optional.of(usuario));
        when(solicitacaoRepository.findByIdAndUsuarioId(eq(999L), eq(1L))).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
            solicitacaoService.cancelarSolicitacao(999L, request, "teste@empresa.com")
        );
    }
}
