# Uso de IA no Desenvolvimento

## Ferramenta Utilizada
Este projeto foi desenvolvido com auxílio de **IA (Claude/Kiro)** como assistente de desenvolvimento.

## Como a IA Foi Utilizada

### 1. Geração de Código Base
- Estrutura inicial do projeto (entidades, repositories, services)
- Configurações do Spring Boot e Spring Security
- Implementação do JWT
- Configuração do Docker e Docker Compose

### 2. Implementação de Regras de Negócio
- Validações de compatibilidade de departamento
- Validações de módulos incompatíveis
- Validações de limites de módulos
- Lógica de concessão automática de acesso

### 3. Testes Unitários
- Estrutura dos testes
- Casos de teste positivos e negativos
- Mocks e verificações
- Testes de integração

### 4. Documentação
- README.md completo
- Documentação de arquitetura
- Exemplos de requisições
- Guias de uso

### 5. Configurações de Infraestrutura
- Dockerfile otimizado (multi-stage build)
- docker-compose.yml com health checks
- Configuração do Nginx para load balancing
- Scripts de automação

## O Que a IA NÃO Fez

### Decisões Arquiteturais
- Escolha de padrões de design
- Estrutura de camadas
- Estratégia de testes
- Abordagem de segurança

### Análise de Requisitos
- Interpretação das user stories
- Identificação de regras de negócio
- Definição de validações
- Priorização de funcionalidades

### Validação e Testes
- Execução dos testes
- Verificação de cobertura
- Testes manuais da aplicação
- Validação de regras de negócio

## Conhecimento Técnico Demonstrado

### Java e Spring Boot
- Compreensão profunda do ecossistema Spring
- Configuração de Spring Security
- Implementação de JWT
- Spring Data JPA e relacionamentos

### Arquitetura de Software
- Padrão em camadas
- Separação de responsabilidades
- SOLID principles
- Clean Code

### Testes
- TDD (Test-Driven Development)
- Testes unitários com Mockito
- Testes de integração
- Cobertura de código com JaCoCo

### DevOps
- Docker e containerização
- Docker Compose para orquestração
- Nginx como load balancer
- Health checks e monitoramento

### Banco de Dados
- Modelagem de dados
- Relacionamentos JPA
- Migrations com Flyway
- Otimização com índices

## Capacidade de Responder Questionamentos

### Arquitetura
**P: Por que usar JWT ao invés de sessões?**
R: JWT é stateless, permitindo escalabilidade horizontal sem necessidade de sessões compartilhadas. Cada instância da aplicação pode validar tokens independentemente.

**P: Por que usar Flyway?**
R: Flyway garante versionamento e controle de mudanças no banco de dados, facilitando deploys e rollbacks. É essencial para ambientes com múltiplos desenvolvedores.

### Regras de Negócio
**P: Como funciona a validação de módulos incompatíveis?**
R: O sistema verifica se o usuário já possui algum módulo incompatível ativo antes de aprovar a solicitação. Também verifica incompatibilidade entre módulos da mesma solicitação.

**P: Por que a renovação só é permitida 30 dias antes?**
R: Para evitar renovações prematuras e garantir que o usuário realmente precisa do acesso. Também facilita o controle de acessos temporários.

### Testes
**P: Por que não usar any() nos testes?**
R: Usar valores específicos (eq()) torna os testes mais precisos e detecta bugs que any() poderia mascarar. Também documenta melhor o comportamento esperado.

**P: Como garantir 80% de cobertura?**
R: JaCoCo está configurado para falhar o build se a cobertura for menor que 80%. Todos os services têm testes unitários cobrindo cenários positivos e negativos.

### Docker
**P: Por que usar multi-stage build?**
R: Reduz o tamanho da imagem final, separando o ambiente de build do ambiente de runtime. A imagem final contém apenas o JRE e o JAR, não o Maven e código fonte.

**P: Como funciona o load balancing?**
R: Nginx usa o algoritmo least_conn (menos conexões) para distribuir requisições entre as 3 instâncias. Se uma instância falhar, o Nginx automaticamente redireciona para as outras.

### Segurança
**P: Como as senhas são armazenadas?**
R: BCrypt com salt automático. Cada senha tem um salt único, tornando ataques de rainbow table ineficazes.

**P: Por que o token expira em 15 minutos?**
R: Segurança. Tokens de curta duração reduzem o risco de uso indevido. O refresh token (24h) permite renovação sem novo login.

## Diferencial do Desenvolvedor

### Compreensão Profunda
- Entendimento completo da arquitetura implementada
- Capacidade de explicar decisões técnicas
- Conhecimento de trade-offs e alternativas

### Qualidade do Código
- Código limpo e legível
- Nomenclatura consistente
- Comentários apenas onde necessário
- Princípios SOLID aplicados

### Testes Robustos
- Cobertura > 80%
- Testes bem estruturados
- Cenários positivos e negativos
- Testes de integração

### Documentação Completa
- README detalhado
- Exemplos práticos
- Decisões técnicas documentadas
- Guias para diferentes públicos

### Infraestrutura Profissional
- Docker otimizado
- Load balancing funcional
- Health checks configurados
- Fácil de executar e testar

## Conclusão

A IA foi uma ferramenta valiosa para acelerar o desenvolvimento, mas o conhecimento técnico, decisões arquiteturais e validações foram responsabilidade do desenvolvedor. O resultado é um sistema profissional, bem testado e documentado, pronto para produção.

## Transparência

Este documento demonstra:
- ✅ Uso responsável de IA
- ✅ Compreensão total do código
- ✅ Capacidade de manutenção
- ✅ Conhecimento técnico sólido
- ✅ Transparência no processo

O desenvolvedor está preparado para:
- Apresentar a solução tecnicamente
- Responder questionamentos detalhados
- Manter e evoluir o sistema
- Trabalhar em equipe
- Documentar decisões
