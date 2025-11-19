# Exemplos de Requisições - Sistema de Acesso a Módulos

## 1. Autenticação

### Login
```bash
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ti@empresa.com",
    "password": "senha123"
  }'
```

**Resposta:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

### Renovar Token
```bash
curl -X POST http://localhost/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

## 2. Módulos

### Listar Módulos Disponíveis
```bash
curl -X GET http://localhost/api/modulos \
  -H "Authorization: Bearer {seu_token}"
```

**Resposta:**
```json
[
  {
    "id": 1,
    "nome": "Portal do Colaborador",
    "descricao": "Portal de acesso geral para colaboradores",
    "ativo": true,
    "departamentosPermitidos": ["TI", "Financeiro", "RH", "Operações", "Outros"],
    "modulosIncompativeis": []
  },
  {
    "id": 4,
    "nome": "Aprovador Financeiro",
    "descricao": "Perfil de aprovador de transações financeiras",
    "ativo": true,
    "departamentosPermitidos": ["Financeiro", "TI"],
    "modulosIncompativeis": [5]
  }
]
```

## 3. Solicitações

### Criar Solicitação (Aprovada)
```bash
curl -X POST http://localhost/api/solicitacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu_token}" \
  -d '{
    "moduloIds": [1, 2],
    "justificativa": "Preciso acessar estes módulos para realizar minhas atividades diárias de suporte técnico e análise de relatórios",
    "urgente": false
  }'
```

**Resposta (Aprovada):**
```json
{
  "mensagem": "Solicitação criada com sucesso! Protocolo: SOL-20241118-0001. Seus acessos já estão disponíveis!",
  "protocolo": "SOL-20241118-0001",
  "solicitacaoId": 1
}
```

### Criar Solicitação (Negada - Departamento Incompatível)
```bash
# Usuário do Financeiro tentando acessar módulo de TI
curl -X POST http://localhost/api/solicitacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token_financeiro}" \
  -d '{
    "moduloIds": [10],
    "justificativa": "Preciso acessar o módulo de auditoria para verificar logs do sistema",
    "urgente": true
  }'
```

**Resposta (Negada):**
```json
{
  "mensagem": "Solicitação negada. Motivo: Departamento sem permissão para acessar este módulo",
  "protocolo": "SOL-20241118-0002",
  "solicitacaoId": 2
}
```

### Listar Solicitações (com filtros)
```bash
# Todas as solicitações
curl -X GET "http://localhost/api/solicitacoes?page=0&size=10" \
  -H "Authorization: Bearer {seu_token}"

# Filtrar por status
curl -X GET "http://localhost/api/solicitacoes?status=ATIVO&page=0&size=10" \
  -H "Authorization: Bearer {seu_token}"

# Filtrar por texto (protocolo ou nome do módulo)
curl -X GET "http://localhost/api/solicitacoes?texto=Portal&page=0&size=10" \
  -H "Authorization: Bearer {seu_token}"

# Filtrar por urgente
curl -X GET "http://localhost/api/solicitacoes?urgente=true&page=0&size=10" \
  -H "Authorization: Bearer {seu_token}"

# Filtrar por período
curl -X GET "http://localhost/api/solicitacoes?dataInicio=2024-11-01T00:00:00&dataFim=2024-11-30T23:59:59&page=0&size=10" \
  -H "Authorization: Bearer {seu_token}"
```

**Resposta:**
```json
{
  "content": [
    {
      "id": 1,
      "protocolo": "SOL-20241118-0001",
      "modulos": [
        {"id": 1, "nome": "Portal do Colaborador"},
        {"id": 2, "nome": "Relatórios Gerenciais"}
      ],
      "status": "ATIVO",
      "justificativa": "Preciso acessar estes módulos...",
      "urgente": false,
      "dataSolicitacao": "2024-11-18T10:30:00",
      "dataExpiracao": "2025-05-17T10:30:00",
      "motivoNegacao": null,
      "motivoCancelamento": null
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

### Buscar Detalhes da Solicitação
```bash
curl -X GET http://localhost/api/solicitacoes/1 \
  -H "Authorization: Bearer {seu_token}"
```

**Resposta:**
```json
{
  "id": 1,
  "protocolo": "SOL-20241118-0001",
  "modulos": [
    {"id": 1, "nome": "Portal do Colaborador"},
    {"id": 2, "nome": "Relatórios Gerenciais"}
  ],
  "status": "ATIVO",
  "justificativa": "Preciso acessar estes módulos...",
  "urgente": false,
  "dataSolicitacao": "2024-11-18T10:30:00",
  "dataExpiracao": "2025-05-17T10:30:00",
  "motivoNegacao": null,
  "motivoCancelamento": null,
  "historico": [
    {
      "descricao": "Solicitação aprovada automaticamente",
      "dataHora": "2024-11-18T10:30:00"
    }
  ]
}
```

### Cancelar Solicitação
```bash
curl -X PUT http://localhost/api/solicitacoes/1/cancelar \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu_token}" \
  -d '{
    "motivo": "Não preciso mais deste acesso pois mudei de função"
  }'
```

**Resposta:** 204 No Content

### Renovar Acesso
```bash
curl -X POST http://localhost/api/solicitacoes/1/renovar \
  -H "Authorization: Bearer {seu_token}"
```

**Resposta:**
```json
{
  "mensagem": "Solicitação criada com sucesso! Protocolo: SOL-20241118-0003. Seus acessos já estão disponíveis!",
  "protocolo": "SOL-20241118-0003",
  "solicitacaoId": 3
}
```

## 4. Cenários de Erro

### Validação - Justificativa Genérica
```bash
curl -X POST http://localhost/api/solicitacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu_token}" \
  -d '{
    "moduloIds": [1],
    "justificativa": "teste",
    "urgente": false
  }'
```

**Resposta:** 400 Bad Request
```json
{
  "timestamp": "2024-11-18T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Erro de validação nos dados enviados",
  "path": "/api/solicitacoes",
  "details": [
    "justificativa: Justificativa insuficiente ou genérica"
  ]
}
```

### Validação - Justificativa Curta
```bash
curl -X POST http://localhost/api/solicitacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu_token}" \
  -d '{
    "moduloIds": [1],
    "justificativa": "Preciso",
    "urgente": false
  }'
```

**Resposta:** 400 Bad Request
```json
{
  "timestamp": "2024-11-18T10:30:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Erro de validação nos dados enviados",
  "path": "/api/solicitacoes",
  "details": [
    "justificativa: Justificativa deve ter entre 20 e 500 caracteres"
  ]
}
```

### Autenticação - Credenciais Inválidas
```bash
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ti@empresa.com",
    "password": "senhaerrada"
  }'
```

**Resposta:** 401 Unauthorized
```json
{
  "timestamp": "2024-11-18T10:30:00",
  "status": 401,
  "error": "Authentication Failed",
  "message": "Email ou senha inválidos",
  "path": "/api/auth/login"
}
```

### Autorização - Token Inválido
```bash
curl -X GET http://localhost/api/solicitacoes \
  -H "Authorization: Bearer token_invalido"
```

**Resposta:** 401 Unauthorized

### Recurso Não Encontrado
```bash
curl -X GET http://localhost/api/solicitacoes/999 \
  -H "Authorization: Bearer {seu_token}"
```

**Resposta:** 404 Not Found
```json
{
  "timestamp": "2024-11-18T10:30:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Solicitação não encontrada",
  "path": "/api/solicitacoes/999"
}
```

## 5. Testando Balanceamento de Carga

### Script para Testar Load Balancer
```bash
# Windows (PowerShell)
for ($i=1; $i -le 10; $i++) {
  curl -X GET http://localhost/actuator/health
  Write-Host "Requisição $i enviada"
}

# Linux/Mac
for i in {1..10}; do
  curl -X GET http://localhost/actuator/health
  echo "Requisição $i enviada"
done
```

### Verificar Logs do Nginx
```bash
docker logs acesso-nginx
```

## 6. Swagger UI

Acesse a documentação interativa em:
```
http://localhost/swagger-ui.html
```

Você pode testar todos os endpoints diretamente pela interface do Swagger.
