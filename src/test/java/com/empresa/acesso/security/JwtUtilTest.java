package com.empresa.acesso.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    
    private JwtUtil jwtUtil;
    private UserDetails userDetails;
    
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 900000L);
        
        userDetails = new User("teste@empresa.com", "password", new ArrayList<>());
    }
    
    @Test
    void deveGerarTokenComSucesso() {
        String token = jwtUtil.generateToken(userDetails);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
    
    @Test
    void deveExtrairUsernameDoToken() {
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        
        assertEquals("teste@empresa.com", username);
    }
    
    @Test
    void deveValidarTokenValido() {
        String token = jwtUtil.generateToken(userDetails);
        Boolean isValid = jwtUtil.validateToken(token, userDetails);
        
        assertTrue(isValid);
    }
    
    @Test
    void deveInvalidarTokenComUsernameIncorreto() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails outroUsuario = new User("outro@empresa.com", "password", new ArrayList<>());
        
        Boolean isValid = jwtUtil.validateToken(token, outroUsuario);
        
        assertFalse(isValid);
    }
    
    @Test
    void deveExtrairDataExpiracao() {
        String token = jwtUtil.generateToken(userDetails);
        var expiration = jwtUtil.extractExpiration(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.getTime() > System.currentTimeMillis());
    }
}
