# Sistema de Solicitação de Acesso a Módulos


## Processo de Desenvolvimento

Este projeto foi desenvolvido com auxílio de IA (Kiro/Claude) como ferramenta de 
produtividade e pair programming. O uso de IA foi estratégico para:

- Acelerar a implementação de funcionalidades conhecidas
- Validar decisões arquiteturais
- Troubleshooting de problemas técnicos
- Garantir boas práticas e padrões de código

**Minha contribuição:**
- Definição da arquitetura e estrutura do projeto
- Decisões de design e modelagem de dados
- Implementação e adaptação das soluções propostas
- Troubleshooting e resolução de problemas
- Testes e validação de funcionalidades
- Compreensão profunda de cada componente implementado

## Descrição do Projeto
Sistema corporativo para gerenciamento de solicitações de acesso a módulos, com autenticação JWT, validação automática de regras de negócio e concessão automática de acessos.

## 📚 Navegação
- **[SUMARIO.md](SUMARIO.md)** - Índice completo de toda a documentação
- **[INICIO_RAPIDO.md](INICIO_RAPIDO.md)** - Comece aqui! Instruções em 3 passos

## Tecnologias Utilizadas
- Java 21
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL 17
- H2 Database (testes)
- Maven 3.9+
- Docker & Docker Compose
- Nginx (Load Balancer)
- Lombok
- JUnit 5
- Mockito
- JaCoCo (cobertura de testes)
- Flyway (migrations)
- Swagger/OpenAPI

## Pré-requisitos
- Docker 24.0+
- Docker Compose 2.20+

## Como Executar Localmente com Docker

1. Clone o repositório
2. Na raiz do projeto, execute:
```bash
docker-compose up -d
```

3. Aguarde todos os containers subirem (aproximadamente 30-60 segundos)
4. Acesse o Swagger: http://localhost/swagger-ui.html

## Como Executar os Testes

```bash
mvn clean test
```

## Como Visualizar Relatório de Cobertura

```bash
mvn clean test jacoco:report
```

O relatório estará disponível em: `target/site/jacoco/index.html`

## Credenciais para Teste

### Usuários Disponíveis:
- **TI**: ti@empresa.com / senha123
- **Financeiro**: financeiro@empresa.com / senha123
- **RH**: rh@empresa.com / senha123
- **Operações**: operacoes@empresa.com / senha123

## Exemplos de Requisições

### 1. Autenticação
```bash
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ti@empresa.com",
    "password": "senha123"
  }'
```

### 2. Criar Solicitação
```bash
curl -X POST http://localhost/api/solicitacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu_token}" \
  -d '{
    "moduloIds": [1, 2],
    "justificativa": "Preciso acessar estes módulos para realizar minhas atividades diárias de suporte técnico",
    "urgente": false
  }'
```

### 3. Listar Solicitações
```bash
curl -X GET "http://localhost/api/solicitacoes?page=0&size=10" \
  -H "Authorization: Bearer {seu_token}"
```

### 4. Listar Módulos Disponíveis
```bash
curl -X GET http://localhost/api/modulos \
  -H "Authorization: Bearer {seu_token}"
```

## Arquitetura da Solução

### Camadas da Aplicação:
- **Controller**: Endpoints REST
- **Service**: Lógica de negócio
- **Repository**: Acesso a dados
- **Security**: Autenticação e autorização JWT
- **Config**: Configurações do Spring

### Infraestrutura:
```
[Cliente] → [Nginx:80] → [app1:8080, app2:8080, app3:8080] → [PostgreSQL:5432]
```

- **Nginx**: Load balancer com algoritmo round-robin
- **3 Instâncias da Aplicação**: Alta disponibilidade
- **PostgreSQL**: Banco de dados principal
- **Rede Docker**: Comunicação interna entre containers

## Decisões Técnicas

### 1. Autenticação JWT
- Token expira em 15 minutos
- Refresh token implementado (24 horas)
- Senhas com BCrypt (salt automático)

### 2. Validações
- Bean Validation para DTOs
- Validações customizadas para regras de negócio
- Exception handlers globais

### 3. Testes
- Cobertura > 80% (JaCoCo)
- Uso de valores específicos (sem any())
- Instancio para criação de objetos de teste
- H2 in-memory para testes de integração

### 4. Docker
- Multi-stage build para otimização
- Health checks configurados
- Variáveis de ambiente para configuração
- Rede bridge customizada

### 5. Migrations
- Flyway para versionamento do banco
- Scripts SQL organizados por versão
- Dados iniciais populados automaticamente

## Regras de Negócio Implementadas

### Compatibilidade de Departamento:
- TI: Todos os módulos
- Financeiro: Financeiro, Relatórios, Portal
- RH: RH, Relatórios, Portal
- Operações: Estoque, Compras, Relatórios, Portal
- Outros: Portal, Relatórios

### Módulos Mutuamente Exclusivos:
- Aprovador Financeiro ↔ Solicitante Financeiro
- Administrador RH ↔ Colaborador RH

### Limites:
- Máximo 5 módulos por usuário (10 para TI)
- Máximo 3 módulos por solicitação
- Justificativa: 20-500 caracteres

### Validade:
- Acessos expiram em 180 dias
- Renovação disponível 30 dias antes

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/empresa/acesso/
│   │   ├── config/          # Configurações
│   │   ├── controller/      # REST Controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entidades JPA
│   │   ├── exception/       # Exceções customizadas
│   │   ├── repository/      # Repositories
│   │   ├── security/        # JWT e Security
│   │   ├── service/         # Lógica de negócio
│   │   └── validation/      # Validações customizadas
│   └── resources/
│       ├── db/migration/    # Scripts Flyway
│       └── application.yml  # Configurações
└── test/                    # Testes unitários e integração
```

## Uso de IA

Este projeto foi desenvolvido com auxílio de IA (Claude/Cursor) como assistente de desenvolvimento, mantendo total compreensão da arquitetura, decisões técnicas e implementação.

## Status do Projeto

✅ Todos os requisitos obrigatórios implementados
✅ Cobertura de testes > 80%
✅ Docker Compose funcional
✅ Balanceamento de carga operacional
✅ Swagger documentado
✅ Migrations configuradas
✅ Refresh token implementado
✅ Logs estruturados

## Documentação Adicional

- **ARQUITETURA.md**: Diagramas e visão detalhada da arquitetura
- **DECISOES_TECNICAS.md**: Decisões técnicas e justificativas
- **EXEMPLOS_REQUISICOES.md**: Exemplos práticos de uso da API
- **GUIA_IA.md**: Guia para ferramentas de IA auxiliarem no desenvolvimento
- **CHECKLIST_ENTREGA.md**: Checklist completo de verificação

## Scripts Auxiliares

- **gerar-relatorio.bat**: Gera relatório de cobertura de testes
- **testar-aplicacao.bat**: Testa a aplicação automaticamente

## Contato

Para dúvidas ou esclarecimentos sobre a implementação, estou à disposição.
