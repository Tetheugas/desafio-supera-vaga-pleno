package com.empresa.acesso.service;

import com.empresa.acesso.dto.AuthResponse;
import com.empresa.acesso.dto.LoginRequest;
import com.empresa.acesso.dto.RefreshTokenRequest;
import com.empresa.acesso.entity.RefreshToken;
import com.empresa.acesso.entity.Usuario;
import com.empresa.acesso.repository.UsuarioRepository;
import com.empresa.acesso.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private UserDetailsService userDetailsService;
    
    @Mock
    private RefreshTokenService refreshTokenService;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private AuthService authService;
    
    private Usuario usuario;
    private UserDetails userDetails;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpiration", 900000L);
        
        usuario = Usuario.builder()
            .id(1L)
            .email("teste@empresa.com")
            .senha("$2a$10$hashedPassword")
            .nome("Teste")
            .departamento("TI")
            .ativo(true)
            .build();
        
        userDetails = new User("teste@empresa.com", "$2a$10$hashedPassword", new ArrayList<>());
    }
    
    @Test
    void deveRealizarLoginComSucesso() {
        LoginRequest request = LoginRequest.builder()
            .email("teste@empresa.com")
            .password("senha123")
            .build();
        
        RefreshToken refreshToken = RefreshToken.builder()
            .id(1L)
            .token("refresh-token-123")
            .usuario(usuario)
            .dataCriacao(LocalDateTime.now())
            .dataExpiracao(LocalDateTime.now().plusDays(1))
            .build();
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(null);
        when(userDetailsService.loadUserByUsername(eq("teste@empresa.com")))
            .thenReturn(userDetails);
        when(jwtUtil.generateToken(eq(userDetails)))
            .thenReturn("jwt-token-123");
        when(usuarioRepository.findByEmail(eq("teste@empresa.com")))
            .thenReturn(Optional.of(usuario));
        when(refreshTokenService.createRefreshToken(eq(1L)))
            .thenReturn(refreshToken);
        
        AuthResponse response = authService.login(request);
        
        assertNotNull(response);
        assertEquals("jwt-token-123", response.getAccessToken());
        assertEquals("refresh-token-123", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(900L, response.getExpiresIn());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(eq("teste@empresa.com"));
        verify(jwtUtil).generateToken(eq(userDetails));
        verify(refreshTokenService).createRefreshToken(eq(1L));
    }
    
    @Test
    void deveRenovarTokenComSucesso() {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
            .refreshToken("refresh-token-123")
            .build();
        
        RefreshToken refreshToken = RefreshToken.builder()
            .id(1L)
            .token("refresh-token-123")
            .usuario(usuario)
            .dataCriacao(LocalDateTime.now())
            .dataExpiracao(LocalDateTime.now().plusDays(1))
            .build();
        
        when(refreshTokenService.verifyRefreshToken(eq("refresh-token-123")))
            .thenReturn(refreshToken);
        when(userDetailsService.loadUserByUsername(eq("teste@empresa.com")))
            .thenReturn(userDetails);
        when(jwtUtil.generateToken(eq(userDetails)))
            .thenReturn("new-jwt-token-456");
        
        AuthResponse response = authService.refreshToken(request);
        
        assertNotNull(response);
        assertEquals("new-jwt-token-456", response.getAccessToken());
        assertEquals("refresh-token-123", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        
        verify(refreshTokenService).verifyRefreshToken(eq("refresh-token-123"));
        verify(userDetailsService).loadUserByUsername(eq("teste@empresa.com"));
        verify(jwtUtil).generateToken(eq(userDetails));
    }
}
