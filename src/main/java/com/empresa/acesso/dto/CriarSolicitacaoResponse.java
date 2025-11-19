package com.empresa.acesso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarSolicitacaoResponse {
    private String mensagem;
    private String protocolo;
    private Long solicitacaoId;
}
