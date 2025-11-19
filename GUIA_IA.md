# Guia de Uso de IA - Sistema de Acesso a Módulos

## Contexto do Projeto

Este documento serve como guia para ferramentas de IA (Claude Code, Copilot, Cursor, etc.) auxiliarem no desenvolvimento e manutenção deste projeto.

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/empresa/acesso/
│   │   ├── config/          # Configurações (Security, OpenAPI)
│   │   ├── controller/      # REST Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entidades JPA
│   │   ├── exception/       # Exceções customizadas
│   │   ├── repository/      # Repositories JPA
│   │   ├── security/        # JWT e Security
│   │   ├── service/         # Lógica de negócio
│   │   └── validation/      # Validações customizadas
│   └── resources/
│       ├── db/migration/    # Scripts Flyway
│       └── application.yml  # Configurações
└── test/                    # Testes unitários e integração
```

## Padrões de Código

### Nomenclatura
- **Classes**: PascalCase (ex: `SolicitacaoService`)
- **Métodos**: camelCase (ex: `criarSolicitacao`)
- **Constantes**: UPPER_SNAKE_CASE (ex: `DIAS_EXPIRACAO`)
- **Variáveis**: camelCase (ex: `usuario`, `modulo`)

### Anotações Lombok
- `@Data`: Para DTOs simples
- `@Builder`: Para construção de objetos complexos
- `@NoArgsConstructor` e `@AllArgsConstructor`: Para entidades JPA
- `@RequiredArgsConstructor`: Para injeção de dependências

### Validações
- Bean Validation em DTOs (`@NotNull`, `@NotBlank`, `@Size`, etc.)
- Validações customizadas em anotações próprias
- Validações de negócio nos Services

## Regras de Negócio Importantes

### Compatibilidade de Departamento
```java
// TI pode acessar todos os módulos
// Financeiro: Financeiro, Relatórios, Portal
// RH: RH, Relatórios, Portal
// Operações: Estoque, Compras, Relatórios, Portal
// Outros: Portal, Relatórios
```

### Módulos Incompatíveis
```java
// Aprovador Financeiro (4) ↔ Solicitante Financeiro (5)
// Administrador RH (6) ↔ Colaborador RH (7)
```

### Limites de Módulos
```java
// TI: 10 módulos
// Outros: 5 módulos
```

### Validade e Renovação
```java
// Validade: 180 dias
// Renovação: 30 dias antes da expiração
```

## Testes

### Padrões de Teste
```java
// PROIBIDO usar any(), anyString(), anyLong()
when(repository.findById(any())).thenReturn(...); // ❌ ERRADO

// OBRIGATÓRIO usar valores específicos
when(repository.findById(eq(1L))).thenReturn(...); // ✅ CORRETO

// OBRIGATÓRIO verificar chamadas
verify(repository).save(eq(entity)); // ✅ CORRETO
```

### Estrutura de Teste
```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    @BeforeEach
    void setUp() {
        // Preparar dados de teste
    }
    
    @Test
    void deveExecutarComSucesso() {
        // Given
        // When
        // Then
    }
}
```

## Comandos Úteis

### Desenvolvimento
```bash
# Compilar
mvn clean compile

# Executar testes
mvn test

# Gerar relatório de cobertura
mvn clean test jacoco:report

# Executar aplicação
mvn spring-boot:run
```

### Docker
```bash
# Build e subir containers
docker-compose up -d

# Ver logs
docker logs acesso-app1
docker logs acesso-nginx

# Parar containers
docker-compose down

# Rebuild
docker-compose up -d --build
```

## Prompts Úteis para IA

### Criar Novo Endpoint
```
Crie um endpoint REST para [funcionalidade] seguindo os padrões do projeto:
- Controller em controller/
- Service com lógica de negócio
- DTO para request e response
- Validações apropriadas
- Testes unitários com cobertura > 80%
- Documentação Swagger
```

### Adicionar Validação
```
Adicione uma validação de negócio para [regra] no service [NomeService]:
- Método privado para validação
- Retornar motivo de negação se aplicável
- Adicionar testes para cenários positivos e negativos
```

### Criar Teste
```
Crie testes unitários para [Classe] seguindo as regras:
- Usar valores específicos (eq()) ao invés de any()
- Verificar chamadas com verify()
- Cobertura de cenários positivos e negativos
- Usar @ExtendWith(MockitoExtension.class)
```

### Adicionar Migration
```
Crie uma migration Flyway para [alteração]:
- Arquivo em src/main/resources/db/migration/
- Nomenclatura: V[número]__[descricao].sql
- Incluir rollback manual se necessário
```

## Contexto de Segurança

### JWT
- Secret configurável via variável de ambiente
- Expiration: 15 minutos (900000ms)
- Refresh token: 24 horas (86400000ms)

### Endpoints Públicos
```java
"/api/auth/**"
"/swagger-ui/**"
"/v3/api-docs/**"
"/actuator/health"
```

### Endpoints Protegidos
Todos os outros endpoints requerem autenticação JWT.

## Troubleshooting

### Erro de Compilação
1. Verificar versão do Java (deve ser 21)
2. Limpar cache do Maven: `mvn clean`
3. Atualizar dependências: `mvn dependency:resolve`

### Erro de Teste
1. Verificar uso de any() (proibido)
2. Verificar valores específicos com eq()
3. Verificar mocks configurados corretamente

### Erro de Docker
1. Verificar se PostgreSQL está rodando
2. Verificar health checks
3. Ver logs: `docker logs [container]`

## Boas Práticas

### Ao Adicionar Código
1. Seguir padrões existentes
2. Adicionar testes
3. Atualizar documentação
4. Verificar cobertura de testes
5. Testar localmente com Docker

### Ao Modificar Código
1. Executar testes existentes
2. Adicionar novos testes se necessário
3. Verificar impacto em outras partes
4. Atualizar documentação se aplicável

### Ao Corrigir Bugs
1. Criar teste que reproduz o bug
2. Corrigir o código
3. Verificar se teste passa
4. Adicionar teste de regressão

## Referências Rápidas

### Entidades Principais
- `Usuario`: Usuário do sistema
- `Modulo`: Módulo disponível
- `Solicitacao`: Solicitação de acesso
- `RefreshToken`: Token de renovação

### Services Principais
- `AuthService`: Autenticação
- `SolicitacaoService`: Lógica de solicitações
- `ModuloService`: Consulta de módulos
- `RefreshTokenService`: Gerenciamento de tokens

### Repositories
- `UsuarioRepository`
- `ModuloRepository`
- `SolicitacaoRepository`
- `RefreshTokenRepository`

## Contato e Suporte

Para dúvidas sobre o projeto, consulte:
1. README.md - Instruções gerais
2. DECISOES_TECNICAS.md - Decisões arquiteturais
3. EXEMPLOS_REQUISICOES.md - Exemplos de uso
4. Este documento - Guia para IA
