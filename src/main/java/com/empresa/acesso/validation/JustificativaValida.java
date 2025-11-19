package com.empresa.acesso.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = JustificativaValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JustificativaValida {
    String message() default "Justificativa insuficiente ou genérica";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
