package com.empresa.acesso.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JustificativaValidatorTest {
    
    private JustificativaValidator validator;
    
    @Mock
    private ConstraintValidatorContext context;
    
    @BeforeEach
    void setUp() {
        validator = new JustificativaValidator();
    }
    
    @Test
    void deveValidarJustificativaValida() {
        String justificativa = "Preciso acessar este módulo para realizar minhas atividades diárias";
        assertTrue(validator.isValid(justificativa, context));
    }
    
    @Test
    void deveInvalidarJustificativaGenerica() {
        assertFalse(validator.isValid("teste", context));
        assertFalse(validator.isValid("aaa", context));
        assertFalse(validator.isValid("preciso", context));
        assertFalse(validator.isValid("aaaa", context));
    }
    
    @Test
    void deveInvalidarJustificativaVazia() {
        assertFalse(validator.isValid("", context));
        assertFalse(validator.isValid("   ", context));
    }
    
    @Test
    void deveInvalidarJustificativaNula() {
        assertFalse(validator.isValid(null, context));
    }
    
    @Test
    void deveValidarJustificativaComTextoGenericoNoMeio() {
        String justificativa = "Eu preciso acessar este módulo para minhas atividades";
        assertTrue(validator.isValid(justificativa, context));
    }
}
