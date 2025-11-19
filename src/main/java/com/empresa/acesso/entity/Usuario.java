package com.empresa.acesso.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String senha;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String departamento;
    
    @Column(nullable = false)
    private Boolean ativo = true;
    
    @ManyToMany
    @JoinTable(
        name = "usuario_modulos",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "modulo_id")
    )
    @Builder.Default
    private Set<Modulo> modulosAtivos = new HashSet<>();
}
