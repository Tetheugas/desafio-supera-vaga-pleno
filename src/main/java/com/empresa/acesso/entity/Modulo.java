package com.empresa.acesso.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "modulos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Modulo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nome;
    
    @Column(nullable = false, length = 500)
    private String descricao;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @ElementCollection
    @CollectionTable(name = "modulo_departamentos", joinColumns = @JoinColumn(name = "modulo_id"))
    @Column(name = "departamento")
    @Builder.Default
    private Set<String> departamentosPermitidos = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "modulo_incompativeis", joinColumns = @JoinColumn(name = "modulo_id"))
    @Column(name = "modulo_incompativel_id")
    @Builder.Default
    private Set<Long> modulosIncompativeis = new HashSet<>();
}
