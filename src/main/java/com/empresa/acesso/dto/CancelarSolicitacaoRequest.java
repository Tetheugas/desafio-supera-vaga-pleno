package com.empresa.acesso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelarSolicitacaoRequest {
    
    @NotBlank(message = "Motivo do cancelamento é obrigatório")
    @Size(min = 10, max = 200, message = "Motivo deve ter entre 10 e 200 caracteres")
    private String motivo;
}
