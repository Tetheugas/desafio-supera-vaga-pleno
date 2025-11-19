package com.empresa.acesso.service;

import com.empresa.acesso.entity.RefreshToken;
import com.empresa.acesso.entity.Usuario;
import com.empresa.acesso.exception.UnauthorizedException;
import com.empresa.acesso.repository.RefreshTokenRepository;
import com.empresa.acesso.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;
    
    @Transactional
    public RefreshToken createRefreshToken(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
        
        refreshTokenRepository.deleteByUsuarioId(usuarioId);
        
        RefreshToken refreshToken = RefreshToken.builder()
            .usuario(usuario)
            .token(UUID.randomUUID().toString())
            .dataCriacao(LocalDateTime.now())
            .dataExpiracao(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
            .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));
        
        if (refreshToken.isExpirado()) {
            throw new UnauthorizedException("Refresh token expirado");
        }
        
        return refreshToken;
    }
}
