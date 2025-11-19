package com.empresa.acesso.service;

import com.empresa.acesso.dto.AuthResponse;
import com.empresa.acesso.dto.LoginRequest;
import com.empresa.acesso.dto.RefreshTokenRequest;
import com.empresa.acesso.entity.RefreshToken;
import com.empresa.acesso.entity.Usuario;
import com.empresa.acesso.exception.UnauthorizedException;
import com.empresa.acesso.repository.UsuarioRepository;
import com.empresa.acesso.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final UsuarioRepository usuarioRepository;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario.getId());
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getToken())
            .tokenType("Bearer")
            .expiresIn(jwtExpiration / 1000)
            .build();
    }
    
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(
            refreshToken.getUsuario().getEmail()
        );
        String accessToken = jwtUtil.generateToken(userDetails);
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken.getToken())
            .tokenType("Bearer")
            .expiresIn(jwtExpiration / 1000)
            .build();
    }
}
