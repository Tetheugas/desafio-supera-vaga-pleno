package com.empresa.acesso.entity;

import com.empresa.acesso.entity.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "solicitacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String protocolo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToMany
    @JoinTable(
        name = "solicitacao_modulos",
        joinColumns = @JoinColumn(name = "solicitacao_id"),
        inverseJoinColumns = @JoinColumn(name = "modulo_id")
    )
    @Builder.Default
    private Set<Modulo> modulos = new HashSet<>();
    
    @Column(nullable = false, length = 500)
    private String justificativa;
    
    @Column(nullable = false)
    private Boolean urgente = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao status;
    
    @Column(length = 500)
    private String motivoNegacao;
    
    @Column(length = 200)
    private String motivoCancelamento;
    
    @Column(nullable = false)
    private LocalDateTime dataSolicitacao;
    
    private LocalDateTime dataExpiracao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_origem_id")
    private Solicitacao solicitacaoOrigem;
    
    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistoricoSolicitacao> historico = new ArrayList<>();
    
    public void adicionarHistorico(String descricao) {
        HistoricoSolicitacao hist = HistoricoSolicitacao.builder()
            .solicitacao(this)
            .descricao(descricao)
            .dataHora(LocalDateTime.now())
            .build();
        this.historico.add(hist);
    }
}
