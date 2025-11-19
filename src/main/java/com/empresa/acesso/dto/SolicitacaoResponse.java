package com.empresa.acesso.dto;

import com.empresa.acesso.entity.enums.StatusSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoResponse {
    private Long id;
    private String protocolo;
    private List<ModuloSimpleResponse> modulos;
    private StatusSolicitacao status;
    private String justificativa;
    private Boolean urgente;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataExpiracao;
    private String motivoNegacao;
    private String motivoCancelamento;
}
