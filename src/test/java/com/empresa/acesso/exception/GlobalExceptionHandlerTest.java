package com.empresa.acesso.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    
    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;
    
    @Mock
    private HttpServletRequest request;
    
    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }
    
    @Test
    void deveTratarBusinessException() {
        BusinessException ex = new BusinessException("Erro de negócio");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(ex, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro de negócio", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
    }
    
    @Test
    void deveTratarResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso não encontrado");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(ex, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Recurso não encontrado", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
    }
    
    @Test
    void deveTratarUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Não autorizado");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorizedException(ex, request);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Não autorizado", response.getBody().getMessage());
        assertEquals(401, response.getBody().getStatus());
    }
    
    @Test
    void deveTratarBadCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Credenciais inválidas");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(ex, request);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email ou senha inválidos", response.getBody().getMessage());
        assertEquals(401, response.getBody().getStatus());
    }
    
    @Test
    void deveTratarMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "mensagem de erro");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro de validação nos dados enviados", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getDetails());
        assertFalse(response.getBody().getDetails().isEmpty());
    }
    
    @Test
    void deveTratarGenericException() {
        Exception ex = new Exception("Erro genérico");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro interno do servidor", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
    }
}
