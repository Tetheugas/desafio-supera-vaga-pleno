# Troubleshooting - Guia de Resolução de Problemas

## 🔧 Problemas Comuns e Soluções

### 1. Docker Compose Não Sobe

#### Problema: "Port already in use"
```
Error: bind: address already in use
```

**Solução:**
```bash
# Verificar o que está usando a porta
netstat -ano | findstr :80
netstat -ano | findstr :5432

# Parar o processo ou mudar a porta no docker-compose.yml
# Opção 1: Parar o processo
taskkill /PID [numero_do_pid] /F

# Opção 2: Mudar porta no docker-compose.yml
# nginx: ports: - "8080:80"
```

#### Problema: "Cannot connect to Docker daemon"
```
Error: Cannot connect to the Docker daemon
```

**Solução:**
```bash
# Iniciar Docker Desktop
# Ou verificar se o serviço está rodando
docker ps
```

#### Problema: "No space left on device"
```
Error: no space left on device
```

**Solução:**
```bash
# Limpar imagens não utilizadas
docker system prune -a

# Limpar volumes
docker volume prune
```

---

### 2. Containers Não Ficam Healthy

#### Problema: PostgreSQL não fica healthy
```
postgres | unhealthy
```

**Solução:**
```bash
# Ver logs do PostgreSQL
docker logs acesso-postgres

# Aguardar mais tempo (pode demorar 30-60s)
timeout /t 60

# Verificar novamente
docker ps
```

#### Problema: App não fica healthy
```
app1 | unhealthy
```

**Solução:**
```bash
# Ver logs da aplicação
docker logs acesso-app1

# Verificar se PostgreSQL está rodando
docker ps | findstr postgres

# Verificar conexão com banco
docker exec acesso-app1 wget -O- http://localhost:8080/actuator/health
```

---

### 3. Erros de Compilação

#### Problema: "Java version mismatch"
```
Error: Java version must be 21
```

**Solução:**
```bash
# Verificar versão do Java
java -version

# Instalar Java 21 se necessário
# Download: https://adoptium.net/
```

#### Problema: "Cannot resolve dependencies"
```
Error: Could not resolve dependencies
```

**Solução:**
```bash
# Limpar cache do Maven
mvn clean

# Forçar atualização de dependências
mvn dependency:resolve -U

# Deletar pasta .m2 se necessário
# Windows: C:\Users\[usuario]\.m2\repository
```

#### Problema: "Lombok not working"
```
Error: Cannot find symbol
```

**Solução:**
```bash
# Instalar plugin Lombok na IDE
# IntelliJ: Settings > Plugins > Lombok
# Eclipse: Download lombok.jar e executar

# Habilitar annotation processing
# IntelliJ: Settings > Build > Compiler > Annotation Processors
```

---

### 4. Erros de Testes

#### Problema: "Tests failing"
```
Error: Tests run: 10, Failures: 2
```

**Solução:**
```bash
# Ver detalhes do erro
mvn test

# Executar teste específico
mvn test -Dtest=NomeDoTeste

# Limpar e executar novamente
mvn clean test
```

#### Problema: "H2 database error"
```
Error: Table not found
```

**Solução:**
```bash
# Verificar application-test.yml
# Garantir que flyway.enabled=false
# Garantir que ddl-auto=create-drop
```

#### Problema: "Coverage below 80%"
```
Error: Coverage 75% is below minimum 80%
```

**Solução:**
```bash
# Ver relatório de cobertura
mvn clean test jacoco:report
# Abrir: target/site/jacoco/index.html

# Adicionar testes para classes com baixa cobertura
```

---

### 5. Erros de Autenticação

#### Problema: "401 Unauthorized"
```
{
  "status": 401,
  "message": "Email ou senha inválidos"
}
```

**Solução:**
```bash
# Verificar credenciais
# Email: ti@empresa.com
# Senha: senha123

# Verificar se usuário existe no banco
docker exec -it acesso-postgres psql -U postgres -d acesso_modulos
SELECT * FROM usuarios;
```

#### Problema: "Token expired"
```
{
  "status": 401,
  "message": "Token expirado"
}
```

**Solução:**
```bash
# Fazer login novamente
# Ou usar refresh token

POST /api/auth/refresh
{
  "refreshToken": "seu-refresh-token"
}
```

#### Problema: "Invalid token"
```
{
  "status": 401,
  "message": "Token inválido"
}
```

**Solução:**
```bash
# Verificar formato do header
# Deve ser: Authorization: Bearer {token}
# NÃO: Authorization: {token}

# Verificar se token não tem espaços extras
```

---

### 6. Erros de Validação

#### Problema: "Justificativa genérica"
```
{
  "message": "Justificativa insuficiente ou genérica"
}
```

**Solução:**
```bash
# Usar justificativa válida (20-500 caracteres)
# NÃO usar: "teste", "aaa", "preciso"
# Usar: "Preciso acessar estes módulos para..."
```

#### Problema: "Módulo não encontrado"
```
{
  "message": "Um ou mais módulos não foram encontrados"
}
```

**Solução:**
```bash
# Listar módulos disponíveis
GET /api/modulos

# Usar IDs válidos
# Exemplo: [1, 2] ao invés de [999]
```

---

### 7. Erros de Regras de Negócio

#### Problema: "Departamento sem permissão"
```
{
  "message": "Solicitação negada. Motivo: Departamento sem permissão..."
}
```

**Solução:**
```bash
# Verificar compatibilidade de departamento
# TI: Todos os módulos
# Financeiro: Financeiro, Relatórios, Portal
# RH: RH, Relatórios, Portal
# Operações: Estoque, Compras, Relatórios, Portal

# Usar módulo compatível com seu departamento
```

#### Problema: "Módulo incompatível"
```
{
  "message": "Solicitação negada. Motivo: Módulo incompatível..."
}
```

**Solução:**
```bash
# Verificar módulos incompatíveis
# Aprovador Financeiro (4) ↔ Solicitante Financeiro (5)
# Administrador RH (6) ↔ Colaborador RH (7)

# Cancelar solicitação do módulo incompatível primeiro
PUT /api/solicitacoes/{id}/cancelar
```

#### Problema: "Limite de módulos atingido"
```
{
  "message": "Solicitação negada. Motivo: Limite de módulos ativos atingido"
}
```

**Solução:**
```bash
# Verificar limite
# TI: 10 módulos
# Outros: 5 módulos

# Cancelar alguns módulos antes de solicitar novos
```

---

### 8. Erros de Banco de Dados

#### Problema: "Connection refused"
```
Error: Connection to localhost:5432 refused
```

**Solução:**
```bash
# Verificar se PostgreSQL está rodando
docker ps | findstr postgres

# Iniciar PostgreSQL se necessário
docker-compose up -d postgres

# Aguardar ficar healthy
docker ps
```

#### Problema: "Flyway migration failed"
```
Error: Migration failed
```

**Solução:**
```bash
# Ver logs
docker logs acesso-app1

# Limpar banco e recriar
docker-compose down -v
docker-compose up -d

# Ou executar migrations manualmente
docker exec -it acesso-postgres psql -U postgres -d acesso_modulos
# Executar scripts em src/main/resources/db/migration/
```

---

### 9. Erros de Swagger

#### Problema: "Swagger não carrega"
```
404 Not Found
```

**Solução:**
```bash
# Aguardar aplicação subir completamente (~60s)
timeout /t 60

# Verificar se aplicação está rodando
docker ps

# Acessar URL correta
http://localhost/swagger-ui.html
# NÃO: http://localhost:8080/swagger-ui.html
```

#### Problema: "Endpoints não aparecem"
```
No operations defined in spec!
```

**Solução:**
```bash
# Verificar logs da aplicação
docker logs acesso-app1

# Verificar se controllers estão anotados corretamente
# @RestController
# @RequestMapping("/api/...")
```

---

### 10. Erros de Load Balancer

#### Problema: "502 Bad Gateway"
```
502 Bad Gateway
nginx/1.25
```

**Solução:**
```bash
# Verificar se aplicações estão rodando
docker ps

# Ver logs do Nginx
docker logs acesso-nginx

# Verificar health das aplicações
curl http://localhost/actuator/health
```

#### Problema: "Sempre vai para mesma instância"
```
Sempre app1 responde
```

**Solução:**
```bash
# Verificar configuração do Nginx
# Deve ter: upstream backend { ... }

# Verificar se todas as instâncias estão healthy
docker ps

# Fazer múltiplas requisições para testar
for i in {1..10}; do curl http://localhost/actuator/health; done
```

---

## 🔍 Comandos de Diagnóstico

### Ver Logs
```bash
# Todos os containers
docker-compose logs

# Container específico
docker logs acesso-app1
docker logs acesso-nginx
docker logs acesso-postgres

# Seguir logs em tempo real
docker logs -f acesso-app1
```

### Verificar Status
```bash
# Status dos containers
docker ps

# Status detalhado
docker ps -a

# Uso de recursos
docker stats
```

### Acessar Container
```bash
# Bash no container
docker exec -it acesso-app1 sh

# PostgreSQL
docker exec -it acesso-postgres psql -U postgres -d acesso_modulos
```

### Testar Conectividade
```bash
# Health check
curl http://localhost/actuator/health

# Login
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ti@empresa.com","password":"senha123"}'

# Ping entre containers
docker exec acesso-app1 ping postgres
```

---

## 🆘 Quando Tudo Falha

### Reset Completo
```bash
# 1. Parar tudo
docker-compose down -v

# 2. Limpar imagens
docker system prune -a

# 3. Rebuild
docker-compose build --no-cache

# 4. Subir novamente
docker-compose up -d

# 5. Aguardar
timeout /t 60

# 6. Verificar
docker ps
```

### Executar Localmente (sem Docker)
```bash
# 1. Subir apenas PostgreSQL
docker-compose up -d postgres

# 2. Executar aplicação localmente
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. Acessar
http://localhost:8080/swagger-ui.html
```

---

## 📞 Suporte

### Logs Importantes
Ao reportar problemas, incluir:
1. Saída de `docker ps`
2. Logs relevantes (`docker logs`)
3. Mensagem de erro completa
4. Passos para reproduzir

### Informações do Sistema
```bash
# Versão do Docker
docker --version

# Versão do Docker Compose
docker-compose --version

# Versão do Java
java -version

# Versão do Maven
mvn --version

# Sistema Operacional
# Windows: winver
# Linux: uname -a
```

---

## ✅ Checklist de Verificação

Antes de reportar problema, verificar:
- [ ] Docker está rodando
- [ ] Portas 80 e 5432 estão livres
- [ ] Aguardou 60 segundos após `docker-compose up`
- [ ] Todos os containers estão healthy
- [ ] Logs não mostram erros críticos
- [ ] Credenciais estão corretas
- [ ] URL está correta (http://localhost)

---

**Se o problema persistir, consulte a documentação ou abra uma issue!** 🚀
