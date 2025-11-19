package com.empresa.acesso.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuloResponse {
    private Long id;
    private String nome;
    private String descricao;
    private Boolean ativo;
    private Set<String> departamentosPermitidos;
    private Set<Long> modulosIncompativeis;
}
