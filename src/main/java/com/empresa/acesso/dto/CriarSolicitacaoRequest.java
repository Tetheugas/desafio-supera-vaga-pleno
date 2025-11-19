package com.empresa.acesso.dto;

import com.empresa.acesso.validation.JustificativaValida;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarSolicitacaoRequest {
    
    @NotEmpty(message = "Deve selecionar pelo menos um módulo")
    @Size(min = 1, max = 3, message = "Deve selecionar entre 1 e 3 módulos")
    private Set<Long> moduloIds;
    
    @NotNull(message = "Justificativa é obrigatória")
    @Size(min = 20, max = 500, message = "Justificativa deve ter entre 20 e 500 caracteres")
    @JustificativaValida
    private String justificativa;
    
    @NotNull(message = "Campo urgente é obrigatório")
    private Boolean urgente;
}
