package com.empresa.acesso.controller;

import com.empresa.acesso.dto.AuthResponse;
import com.empresa.acesso.dto.LoginRequest;
import com.empresa.acesso.dto.RefreshTokenRequest;
import com.empresa.acesso.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private AuthService authService;
    
    @Test
    void deveRealizarLoginComSucesso() throws Exception {
        LoginRequest request = LoginRequest.builder()
            .email("teste@empresa.com")
            .password("senha123")
            .build();
        
        AuthResponse response = AuthResponse.builder()
            .accessToken("jwt-token")
            .refreshToken("refresh-token")
            .tokenType("Bearer")
            .expiresIn(900L)
            .build();
        
        when(authService.login(any(LoginRequest.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("jwt-token"))
            .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
        
        verify(authService).login(any(LoginRequest.class));
    }
    
    @Test
    void deveRenovarTokenComSucesso() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
            .refreshToken("refresh-token")
            .build();
        
        AuthResponse response = AuthResponse.builder()
            .accessToken("new-jwt-token")
            .refreshToken("refresh-token")
            .tokenType("Bearer")
            .expiresIn(900L)
            .build();
        
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("new-jwt-token"));
        
        verify(authService).refreshToken(any(RefreshTokenRequest.class));
    }
}
