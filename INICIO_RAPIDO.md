# Início Rápido - Sistema de Acesso a Módulos

## 🚀 Em 3 Passos

### 1. Subir a Aplicação
```bash
docker-compose up -d
```

### 2. Aguardar (~60 segundos)
```bash
# Verificar status
docker ps
```

### 3. Testar
```bash
# Abrir no navegador
http://localhost/swagger-ui.html
```

## 📝 Primeiro Teste

### 1. Fazer Login
No Swagger, vá em **Autenticação** → **POST /api/auth/login**

Clique em "Try it out" e use:
```json
{
  "email": "ti@empresa.com",
  "password": "senha123"
}
```

Copie o `accessToken` da resposta.

### 2. Autorizar no Swagger
Clique no botão **Authorize** (cadeado) no topo da página.

Digite: `Bearer {seu_token_aqui}`

Clique em **Authorize** e depois **Close**.

### 3. Criar uma Solicitação
Vá em **Solicitações** → **POST /api/solicitacoes**

Clique em "Try it out" e use:
```json
{
  "moduloIds": [1, 2],
  "justificativa": "Preciso acessar estes módulos para realizar minhas atividades diárias de suporte técnico",
  "urgente": false
}
```

Clique em **Execute**.

### 4. Ver suas Solicitações
Vá em **Solicitações** → **GET /api/solicitacoes**

Clique em "Try it out" e depois **Execute**.

## 🎯 Pronto!

Você acabou de:
- ✅ Autenticar no sistema
- ✅ Criar uma solicitação de acesso
- ✅ Consultar suas solicitações

## 📚 Próximos Passos

### Testar Outros Usuários
```json
// Financeiro
{"email": "financeiro@empresa.com", "password": "senha123"}

// RH
{"email": "rh@empresa.com", "password": "senha123"}

// Operações
{"email": "operacoes@empresa.com", "password": "senha123"}
```

### Testar Regras de Negócio

#### 1. Departamento Incompatível
Login como Financeiro e tente solicitar módulo 10 (Auditoria - só TI):
```json
{
  "moduloIds": [10],
  "justificativa": "Preciso acessar o módulo de auditoria para verificar logs",
  "urgente": false
}
```
**Resultado:** Negado - "Departamento sem permissão"

#### 2. Módulos Incompatíveis
Login como TI e solicite módulo 4 (Aprovador Financeiro).
Depois tente solicitar módulo 5 (Solicitante Financeiro):
```json
{
  "moduloIds": [5],
  "justificativa": "Preciso acessar o módulo de solicitante financeiro",
  "urgente": false
}
```
**Resultado:** Negado - "Módulo incompatível"

#### 3. Justificativa Genérica
Tente criar solicitação com justificativa "teste":
```json
{
  "moduloIds": [1],
  "justificativa": "teste",
  "urgente": false
}
```
**Resultado:** Erro de validação

#### 4. Limite de Módulos
Login como Financeiro e tente solicitar 6 módulos diferentes.
**Resultado:** Negado - "Limite de módulos atingido"

### Testar Outras Funcionalidades

#### Listar Módulos Disponíveis
**GET /api/modulos**

#### Ver Detalhes de uma Solicitação
**GET /api/solicitacoes/{id}**

#### Cancelar uma Solicitação
**PUT /api/solicitacoes/{id}/cancelar**
```json
{
  "motivo": "Não preciso mais deste acesso"
}
```

#### Renovar Acesso
**POST /api/solicitacoes/{id}/renovar**
(Só funciona 30 dias antes da expiração)

## 🔧 Comandos Úteis

### Ver Logs
```bash
# Aplicação
docker logs acesso-app1
docker logs acesso-app2
docker logs acesso-app3

# Nginx
docker logs acesso-nginx

# PostgreSQL
docker logs acesso-postgres
```

### Parar Aplicação
```bash
docker-compose down
```

### Rebuild
```bash
docker-compose up -d --build
```

### Executar Testes
```bash
mvn test
```

### Gerar Relatório de Cobertura
```bash
mvn clean test jacoco:report
# Abrir: target/site/jacoco/index.html
```

## 🐛 Problemas Comuns

### Containers não sobem
```bash
# Verificar se portas estão em uso
netstat -ano | findstr :80
netstat -ano | findstr :5432

# Limpar e tentar novamente
docker-compose down
docker-compose up -d
```

### Erro de conexão com banco
```bash
# Aguardar mais tempo (PostgreSQL pode demorar)
timeout /t 30

# Verificar logs do PostgreSQL
docker logs acesso-postgres
```

### Token expirado
- Token expira em 15 minutos
- Faça login novamente ou use refresh token

### Swagger não carrega
- Aguarde alguns segundos após containers subirem
- Verifique se Nginx está rodando: `docker ps`

## 📖 Documentação Completa

Para mais detalhes, consulte:
- **README.md** - Documentação completa
- **EXEMPLOS_REQUISICOES.md** - Mais exemplos de requisições
- **ARQUITETURA.md** - Arquitetura do sistema
- **DECISOES_TECNICAS.md** - Decisões técnicas

## ✅ Tudo Funcionando?

Se você conseguiu:
- ✅ Subir os containers
- ✅ Acessar o Swagger
- ✅ Fazer login
- ✅ Criar uma solicitação

**Parabéns! O sistema está funcionando perfeitamente!** 🎉
