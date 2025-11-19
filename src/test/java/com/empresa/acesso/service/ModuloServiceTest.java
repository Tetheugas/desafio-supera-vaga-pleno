package com.empresa.acesso.service;

import com.empresa.acesso.dto.ModuloResponse;
import com.empresa.acesso.entity.Modulo;
import com.empresa.acesso.repository.ModuloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuloServiceTest {
    
    @Mock
    private ModuloRepository moduloRepository;
    
    @InjectMocks
    private ModuloService moduloService;
    
    private Modulo modulo1;
    private Modulo modulo2;
    
    @BeforeEach
    void setUp() {
        modulo1 = Modulo.builder()
            .id(1L)
            .nome("Portal")
            .descricao("Portal do Colaborador")
            .ativo(true)
            .departamentosPermitidos(Set.of("TI", "Financeiro"))
            .modulosIncompativeis(new HashSet<>())
            .build();
        
        modulo2 = Modulo.builder()
            .id(2L)
            .nome("Relatórios")
            .descricao("Relatórios Gerenciais")
            .ativo(true)
            .departamentosPermitidos(Set.of("TI", "RH"))
            .modulosIncompativeis(new HashSet<>())
            .build();
    }
    
    @Test
    void deveListarModulosDisponiveisComSucesso() {
        when(moduloRepository.findByAtivoTrue()).thenReturn(List.of(modulo1, modulo2));
        
        List<ModuloResponse> response = moduloService.listarModulosDisponiveis();
        
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Portal", response.get(0).getNome());
        assertEquals("Relatórios", response.get(1).getNome());
        
        verify(moduloRepository).findByAtivoTrue();
    }
    
    @Test
    void deveRetornarListaVaziaQuandoNaoHaModulosAtivos() {
        when(moduloRepository.findByAtivoTrue()).thenReturn(List.of());
        
        List<ModuloResponse> response = moduloService.listarModulosDisponiveis();
        
        assertNotNull(response);
        assertTrue(response.isEmpty());
        
        verify(moduloRepository).findByAtivoTrue();
    }
}
