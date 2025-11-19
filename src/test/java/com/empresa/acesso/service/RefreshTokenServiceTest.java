package com.empresa.acesso.service;

import com.empresa.acesso.entity.RefreshToken;
import com.empresa.acesso.entity.Usuario;
import com.empresa.acesso.exception.UnauthorizedException;
import com.empresa.acesso.repository.RefreshTokenRepository;
import com.empresa.acesso.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private RefreshTokenService refreshTokenService;
    
    private Usuario usuario;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshExpiration", 86400000L);
        
        usuario = Usuario.builder()
            .id(1L)
            .email("teste@empresa.com")
            .nome("Teste")
            .departamento("TI")
            .ativo(true)
            .build();
    }
    
    @Test
    void deveCriarRefreshTokenComSucesso() {
        when(usuarioRepository.findById(eq(1L))).thenReturn(Optional.of(usuario));
        doNothing().when(refreshTokenRepository).deleteByUsuarioId(eq(1L));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken rt = invocation.getArgument(0);
            rt.setId(1L);
            return rt;
        });
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(1L);
        
        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getToken());
        assertEquals(usuario, refreshToken.getUsuario());
        assertNotNull(refreshToken.getDataCriacao());
        assertNotNull(refreshToken.getDataExpiracao());
        
        verify(usuarioRepository).findById(eq(1L));
        verify(refreshTokenRepository).deleteByUsuarioId(eq(1L));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
    
    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(eq(999L))).thenReturn(Optional.empty());
        
        assertThrows(UnauthorizedException.class, () -> 
            refreshTokenService.createRefreshToken(999L)
        );
        
        verify(usuarioRepository).findById(eq(999L));
    }
    
    @Test
    void deveVerificarRefreshTokenValido() {
        RefreshToken refreshToken = RefreshToken.builder()
            .id(1L)
            .token("valid-token")
            .usuario(usuario)
            .dataCriacao(LocalDateTime.now())
            .dataExpiracao(LocalDateTime.now().plusDays(1))
            .build();
        
        when(refreshTokenRepository.findByToken(eq("valid-token"))).thenReturn(Optional.of(refreshToken));
        
        RefreshToken result = refreshTokenService.verifyRefreshToken("valid-token");
        
        assertNotNull(result);
        assertEquals("valid-token", result.getToken());
        
        verify(refreshTokenRepository).findByToken(eq("valid-token"));
    }
    
    @Test
    void deveLancarExcecaoQuandoRefreshTokenInvalido() {
        when(refreshTokenRepository.findByToken(eq("invalid-token"))).thenReturn(Optional.empty());
        
        assertThrows(UnauthorizedException.class, () -> 
            refreshTokenService.verifyRefreshToken("invalid-token")
        );
        
        verify(refreshTokenRepository).findByToken(eq("invalid-token"));
    }
    
    @Test
    void deveLancarExcecaoQuandoRefreshTokenExpirado() {
        RefreshToken refreshToken = RefreshToken.builder()
            .id(1L)
            .token("expired-token")
            .usuario(usuario)
            .dataCriacao(LocalDateTime.now().minusDays(2))
            .dataExpiracao(LocalDateTime.now().minusDays(1))
            .build();
        
        when(refreshTokenRepository.findByToken(eq("expired-token"))).thenReturn(Optional.of(refreshToken));
        
        assertThrows(UnauthorizedException.class, () -> 
            refreshTokenService.verifyRefreshToken("expired-token")
        );
        
        verify(refreshTokenRepository).findByToken(eq("expired-token"));
    }
}
