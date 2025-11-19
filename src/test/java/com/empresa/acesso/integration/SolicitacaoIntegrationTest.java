package com.empresa.acesso.integration;

import com.empresa.acesso.dto.CriarSolicitacaoRequest;
import com.empresa.acesso.entity.Modulo;
import com.empresa.acesso.entity.Usuario;
import com.empresa.acesso.repository.ModuloRepository;
import com.empresa.acesso.repository.SolicitacaoRepository;
import com.empresa.acesso.repository.UsuarioRepository;
import com.empresa.acesso.service.SolicitacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SolicitacaoIntegrationTest {
    
    @Autowired
    private SolicitacaoService solicitacaoService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ModuloRepository moduloRepository;
    
    @Autowired
    private SolicitacaoRepository solicitacaoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private Usuario usuario;
    private Modulo modulo1;
    private Modulo modulo2;
    
    @BeforeEach
    void setUp() {
        solicitacaoRepository.deleteAll();
        usuarioRepository.deleteAll();
        moduloRepository.deleteAll();
        
        usuario = Usuario.builder()
            .email("teste@empresa.com")
            .senha(passwordEncoder.encode("senha123"))
            .nome("Teste")
            .departamento("TI")
            .ativo(true)
            .modulosAtivos(new HashSet<>())
            .build();
        usuario = usuarioRepository.save(usuario);
        
        modulo1 = Modulo.builder()
            .nome("Portal")
            .descricao("Portal do Colaborador")
            .ativo(true)
            .departamentosPermitidos(Set.of("TI", "Financeiro", "RH", "Operações"))
            .modulosIncompativeis(new HashSet<>())
            .build();
        modulo1 = moduloRepository.save(modulo1);
        
        modulo2 = Modulo.builder()
            .nome("Relatórios")
            .descricao("Relatórios Gerenciais")
            .ativo(true)
            .departamentosPermitidos(Set.of("TI", "Financeiro", "RH", "Operações"))
            .modulosIncompativeis(new HashSet<>())
            .build();
        modulo2 = moduloRepository.save(modulo2);
    }
    
    @Test
    void deveCriarSolicitacaoComSucessoIntegrado() {
        CriarSolicitacaoRequest request = CriarSolicitacaoRequest.builder()
            .moduloIds(Set.of(modulo1.getId(), modulo2.getId()))
            .justificativa("Preciso acessar estes módulos para realizar minhas atividades diárias de trabalho")
            .urgente(false)
            .build();
        
        var response = solicitacaoService.criarSolicitacao(request, "teste@empresa.com");
        
        assertNotNull(response);
        assertTrue(response.getMensagem().contains("Solicitação criada com sucesso"));
        assertNotNull(response.getProtocolo());
        
        Usuario usuarioAtualizado = usuarioRepository.findByEmail("teste@empresa.com").orElseThrow();
        assertEquals(2, usuarioAtualizado.getModulosAtivos().size());
    }
}
