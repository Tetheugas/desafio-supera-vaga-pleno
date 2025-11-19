# Decisões Técnicas - Sistema de Acesso a Módulos

## Arquitetura

### Padrão em Camadas
- **Controller**: Responsável por receber requisições HTTP e delegar para os services
- **Service**: Contém toda a lógica de negócio e validações
- **Repository**: Acesso aos dados usando Spring Data JPA
- **Entity**: Modelos de domínio mapeados para o banco de dados

### Separação de Responsabilidades
- DTOs separados para Request e Response
- Validações em múltiplas camadas (Bean Validation + Business Rules)
- Exception handlers centralizados

## Segurança

### Autenticação JWT
- Token de acesso com expiração de 15 minutos
- Refresh token com expiração de 24 horas
- Senha criptografada com BCrypt (salt automático)
- Secret key configurável via variável de ambiente

### Autorização
- Filtro JWT para validação de tokens
- Endpoints protegidos por padrão
- Usuário só acessa suas próprias solicitações

## Regras de Negócio

### Validação de Compatibilidade
Implementada no método `validarCompatibilidadeDepartamento`:
- Verifica se o departamento do usuário tem permissão para cada módulo
- Retorna motivo de negação específico

### Validação de Incompatibilidade
Implementada no método `validarModulosIncompativeis`:
- Verifica módulos incompatíveis já ativos
- Verifica incompatibilidade entre módulos da mesma solicitação
- Usa Set para performance O(1) nas buscas

### Validação de Limites
Implementada no método `validarLimiteModulos`:
- TI: 10 módulos
- Outros departamentos: 5 módulos
- Considera módulos já ativos + novos módulos

### Geração de Protocolo
Formato: SOL-YYYYMMDD-NNNN
- Data atual no formato YYYYMMDD
- Número sequencial com 4 dígitos
- Único por solicitação

## Banco de Dados

### Flyway Migrations
- V1: Criação de todas as tabelas
- V2: Dados iniciais (usuários e módulos)
- Versionamento controlado
- Rollback manual se necessário

### Relacionamentos
- Usuario ↔ Modulo (ManyToMany): Módulos ativos do usuário
- Solicitacao ↔ Modulo (ManyToMany): Módulos da solicitação
- Solicitacao → Usuario (ManyToOne): Solicitante
- Solicitacao → Solicitacao (ManyToOne): Renovação
- HistoricoSolicitacao → Solicitacao (ManyToOne): Auditoria

### Índices
- Criados para melhorar performance de consultas frequentes
- usuario_id, status, data_solicitacao

## Testes

### Estratégia de Testes
- Testes unitários para Services (lógica de negócio)
- Testes de Controller com MockMvc
- Testes de integração com H2
- Uso de valores específicos (sem any())

### Cobertura
- Meta: 80% mínimo
- JaCoCo configurado para falhar build se < 80%
- Relatório HTML gerado automaticamente

### Mocks
- Mockito para dependências
- Valores específicos com eq()
- Verificação de chamadas com verify()

## Docker

### Multi-stage Build
- Stage 1: Build com Maven
- Stage 2: Runtime com JRE Alpine
- Reduz tamanho da imagem final
- Cache de dependências Maven

### Health Checks
- Endpoint: /actuator/health
- Intervalo: 30s
- Timeout: 3s
- Retries: 3

### Rede Docker
- Bridge network customizada
- Comunicação interna entre containers
- Isolamento de rede

## Load Balancer

### Nginx
- Algoritmo: least_conn (menos conexões)
- 3 instâncias da aplicação
- Health check automático
- Failover configurado (max_fails=3)

### Configuração
- Proxy pass para upstream
- Headers preservados
- Timeouts configurados
- Buffer otimizado

## Logs

### Estrutura
- SLF4J + Logback
- Níveis: ERROR, WARN, INFO, DEBUG
- Formato: timestamp, level, logger, message

### Auditoria
- Histórico de solicitações
- Registro de todas as alterações
- Data/hora de cada evento

## Performance

### Otimizações
- Lazy loading em relacionamentos
- Queries otimizadas com JPQL
- Índices no banco de dados
- Connection pool configurado

### Paginação
- Padrão: 10 registros por página
- Ordenação: Mais recentes primeiro
- Filtros eficientes

## Escalabilidade

### Stateless
- Sem sessão no servidor
- JWT para autenticação
- Múltiplas instâncias sem problemas

### Horizontal Scaling
- Load balancer distribui carga
- Fácil adicionar mais instâncias
- Banco de dados centralizado

## Manutenibilidade

### Código Limpo
- Nomenclatura clara e consistente
- Métodos pequenos e focados
- Comentários apenas quando necessário
- SOLID principles

### Documentação
- Swagger/OpenAPI completo
- README detalhado
- Comentários em código complexo
- Este documento de decisões técnicas

## Melhorias Futuras

### Possíveis Implementações
- Cache com Redis
- Mensageria para notificações
- Auditoria mais detalhada
- Dashboard de métricas
- Rate limiting
- API versioning
