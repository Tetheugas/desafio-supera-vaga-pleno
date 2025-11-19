package com.empresa.acesso.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class JustificativaValidator implements ConstraintValidator<JustificativaValida, String> {
    
    private static final List<String> TEXTOS_GENERICOS = Arrays.asList(
        "teste", "aaa", "preciso", "test", "aaaa", "aaaaa",
        "bbb", "ccc", "xxx", "zzz", "asdf", "qwerty"
    );
    
    @Override
    public boolean isValid(String justificativa, ConstraintValidatorContext context) {
        if (justificativa == null || justificativa.trim().isEmpty()) {
            return false;
        }
        
        String justificativaLower = justificativa.toLowerCase().trim();
        
        for (String textoGenerico : TEXTOS_GENERICOS) {
            if (justificativaLower.equals(textoGenerico) || 
                justificativaLower.matches("^" + textoGenerico + "+$")) {
                return false;
            }
        }
        
        return true;
    }
}
