# Arquitetura do Sistema - Sistema de Acesso a Módulos

## Visão Geral

```
┌─────────────┐
│   Cliente   │
│  (Browser/  │
│   Postman)  │
└──────┬──────┘
       │ HTTP
       ▼
┌─────────────────────────────────────────┐
│          Nginx (Load Balancer)          │
│              Port: 80                   │
│      Algorithm: least_conn              │
└──────┬──────────────┬──────────────┬────┘
       │              │              │
       ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐
│  app1    │   │  app2    │   │  app3    │
│ :8080    │   │ :8080    │   │ :8080    │
└────┬─────┘   └────┬─────┘   └────┬─────┘
     │              │              │
     └──────────────┴──────────────┘
                    │
                    ▼
            ┌───────────────┐
            │  PostgreSQL   │
            │    :5432      │
            └───────────────┘
```

## Camadas da Aplicação

```
┌─────────────────────────────────────────────┐
│              Controller Layer               │
│  - AuthController                           │
│  - SolicitacaoController                    │
│  - ModuloController                         │
│  - HealthController                         │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│               Service Layer                 │
│  - AuthService                              │
│  - SolicitacaoService (Regras de Negócio)  │
│  - ModuloService                            │
│  - RefreshTokenService                      │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│             Repository Layer                │
│  - UsuarioRepository                        │
│  - SolicitacaoRepository                    │
│  - ModuloRepository                         │
│  - RefreshTokenRepository                   │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│              Database Layer                 │
│  - PostgreSQL 17                            │
│  - Flyway Migrations                        │
└─────────────────────────────────────────────┘
```

## Fluxo de Autenticação

```
┌────────┐                                    ┌────────────┐
│Cliente │                                    │  Servidor  │
└───┬────┘                                    └─────┬──────┘
    │                                               │
    │  POST /api/auth/login                        │
    │  {email, password}                           │
    ├──────────────────────────────────────────────>
    │                                               │
    │                    ┌──────────────────────────┤
    │                    │ 1. Validar credenciais   │
    │                    │ 2. Gerar JWT (15min)     │
    │                    │ 3. Criar Refresh Token   │
    │                    └──────────────────────────┤
    │                                               │
    │  {accessToken, refreshToken}                 │
    <──────────────────────────────────────────────┤
    │                                               │
    │  GET /api/solicitacoes                       │
    │  Authorization: Bearer {token}               │
    ├──────────────────────────────────────────────>
    │                                               │
    │                    ┌──────────────────────────┤
    │                    │ 1. Validar JWT           │
    │                    │ 2. Extrair usuário       │
    │                    │ 3. Processar requisição  │
    │                    └──────────────────────────┤
    │                                               │
    │  {solicitacoes}                              │
    <──────────────────────────────────────────────┤
    │                                               │
```

## Fluxo de Criação de Solicitação

```
┌────────┐                                    ┌─────────────────┐
│Cliente │                                    │SolicitacaoService│
└───┬────┘                                    └────────┬─────────┘
    │                                                  │
    │  POST /api/solicitacoes                         │
    │  {moduloIds, justificativa, urgente}            │
    ├─────────────────────────────────────────────────>
    │                                                  │
    │                         ┌────────────────────────┤
    │                         │ 1. Buscar usuário      │
    │                         └────────────────────────┤
    │                                                  │
    │                         ┌────────────────────────┤
    │                         │ 2. Buscar módulos      │
    │                         └────────────────────────┤
    │                                                  │
    │                         ┌────────────────────────┤
    │                         │ 3. Validar módulos     │
    │                         │    - Ativos?           │
    │                         │    - Sem solicitação?  │
    │                         │    - Sem acesso?       │
    │                         └────────────────────────┤
    │                                                  │
    │                         ┌────────────────────────┤
    │                         │ 4. Validar regras      │
    │                         │    - Departamento?     │
    │                         │    - Incompatível?     │
    │                         │    - Limite?           │
    │                         └────────────────────────┤
    │                                                  │
    │                         ┌────────────────────────┤
    │                         │ 5. Gerar protocolo     │
    │                         └────────────────────────┤
    │                                                  │
    │                         ┌────────────────────────┤
    │                         │ 6. Criar solicitação   │
    │                         │    - ATIVO ou NEGADO   │
    │                         └────────────────────────┤
    │                                                  │
    │                         ┌────────────────────────┤
    │                         │ 7. Conceder acesso     │
    │                         │    (se aprovado)       │
    │                         └────────────────────────┤
    │                                                  │
    │  {mensagem, protocolo, solicitacaoId}           │
    <─────────────────────────────────────────────────┤
    │                                                  │
```

## Modelo de Dados

```
┌─────────────────┐         ┌─────────────────┐
│    Usuario      │         │     Modulo      │
├─────────────────┤         ├─────────────────┤
│ id (PK)         │         │ id (PK)         │
│ email           │         │ nome            │
│ senha           │         │ descricao       │
│ nome            │         │ ativo           │
│ departamento    │         │ departamentos[] │
│ ativo           │         │ incompativeis[] │
└────────┬────────┘         └────────┬────────┘
         │                           │
         │    ┌──────────────────┐   │
         └────┤ usuario_modulos  ├───┘
              │ (ManyToMany)     │
              └──────────────────┘
         │                           │
         │    ┌──────────────────┐   │
         ├────┤  Solicitacao     ├───┘
         │    ├──────────────────┤
         │    │ id (PK)          │
         │    │ protocolo        │
         │    │ usuario_id (FK)  │
         │    │ justificativa    │
         │    │ urgente          │
         │    │ status           │
         │    │ data_solicitacao │
         │    │ data_expiracao   │
         │    └────────┬─────────┘
         │             │
         │    ┌────────┴─────────┐
         │    │ Historico        │
         │    ├──────────────────┤
         │    │ id (PK)          │
         │    │ solicitacao_id   │
         │    │ descricao        │
         │    │ data_hora        │
         │    └──────────────────┘
         │
         │    ┌──────────────────┐
         └────┤ RefreshToken     │
              ├──────────────────┤
              │ id (PK)          │
              │ token            │
              │ usuario_id (FK)  │
              │ data_criacao     │
              │ data_expiracao   │
              └──────────────────┘
```

## Segurança

```
┌─────────────────────────────────────────────┐
│         Security Filter Chain               │
├─────────────────────────────────────────────┤
│                                             │
│  1. JwtAuthenticationFilter                │
│     - Extrai token do header               │
│     - Valida token                         │
│     - Autentica usuário                    │
│                                             │
│  2. UsernamePasswordAuthenticationFilter   │
│     - Processa login                       │
│                                             │
│  3. ExceptionTranslationFilter             │
│     - Trata exceções de segurança          │
│                                             │
│  4. FilterSecurityInterceptor              │
│     - Autoriza acesso aos endpoints        │
│                                             │
└─────────────────────────────────────────────┘
```

## Infraestrutura Docker

```
┌─────────────────────────────────────────────┐
│           Docker Network (Bridge)           │
│                acesso-network               │
├─────────────────────────────────────────────┤
│                                             │
│  ┌──────────────────────────────────────┐  │
│  │  Nginx Container                     │  │
│  │  - Image: nginx:alpine               │  │
│  │  - Port: 80:80                       │  │
│  │  - Config: nginx.conf                │  │
│  └──────────────────────────────────────┘  │
│                                             │
│  ┌──────────────────────────────────────┐  │
│  │  App1 Container                      │  │
│  │  - Build: Dockerfile                 │  │
│  │  - Port: 8080 (internal)             │  │
│  │  - Health: /actuator/health          │  │
│  └──────────────────────────────────────┘  │
│                                             │
│  ┌──────────────────────────────────────┐  │
│  │  App2 Container                      │  │
│  │  - Build: Dockerfile                 │  │
│  │  - Port: 8080 (internal)             │  │
│  │  - Health: /actuator/health          │  │
│  └──────────────────────────────────────┘  │
│                                             │
│  ┌──────────────────────────────────────┐  │
│  │  App3 Container                      │  │
│  │  - Build: Dockerfile                 │  │
│  │  - Port: 8080 (internal)             │  │
│  │  - Health: /actuator/health          │  │
│  └──────────────────────────────────────┘  │
│                                             │
│  ┌──────────────────────────────────────┐  │
│  │  PostgreSQL Container                │  │
│  │  - Image: postgres:17-alpine         │  │
│  │  - Port: 5432:5432                   │  │
│  │  - Volume: postgres_data             │  │
│  │  - Health: pg_isready                │  │
│  └──────────────────────────────────────┘  │
│                                             │
└─────────────────────────────────────────────┘
```

## Padrões de Design Utilizados

### 1. Layered Architecture
- Separação clara entre Controller, Service, Repository
- Cada camada com responsabilidade específica

### 2. Dependency Injection
- Spring IoC Container
- Constructor injection com Lombok @RequiredArgsConstructor

### 3. Repository Pattern
- Spring Data JPA
- Abstração do acesso a dados

### 4. DTO Pattern
- Separação entre entidades e objetos de transferência
- Request/Response específicos

### 5. Builder Pattern
- Lombok @Builder
- Construção fluente de objetos

### 6. Strategy Pattern
- Validações de regras de negócio
- Métodos privados específicos para cada validação

### 7. Template Method
- Spring Security Filter Chain
- Flyway Migrations

## Escalabilidade

### Horizontal Scaling
```
┌─────────────────────────────────────────────┐
│  Adicionar mais instâncias é simples:       │
│                                             │
│  1. Editar docker-compose.yml               │
│  2. Adicionar app4, app5, etc.              │
│  3. Atualizar nginx.conf upstream           │
│  4. docker-compose up -d                    │
│                                             │
│  Sem necessidade de:                        │
│  - Sessões compartilhadas (stateless)       │
│  - Sincronização de cache                   │
│  - Configuração complexa                    │
└─────────────────────────────────────────────┘
```

### Vertical Scaling
```
┌─────────────────────────────────────────────┐
│  Aumentar recursos dos containers:          │
│                                             │
│  services:                                  │
│    app1:                                    │
│      deploy:                                │
│        resources:                           │
│          limits:                            │
│            cpus: '2'                        │
│            memory: 2G                       │
└─────────────────────────────────────────────┘
```

## Monitoramento

### Health Checks
- Endpoint: /actuator/health
- Verificação automática pelo Docker
- Nginx remove instâncias não saudáveis

### Logs
- Estruturados com SLF4J
- Acessíveis via docker logs
- Histórico de solicitações no banco

## Considerações de Performance

### Database
- Índices em colunas frequentemente consultadas
- Lazy loading em relacionamentos
- Connection pool configurado

### Caching
- Potencial para Redis (não implementado)
- Cache de módulos disponíveis
- Cache de departamentos permitidos

### Load Balancing
- Algoritmo least_conn
- Distribuição eficiente de carga
- Failover automático
