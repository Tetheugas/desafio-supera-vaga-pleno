# Comandos Rápidos - Referência

## 🚀 Execução

### Subir Aplicação
```bash
docker-compose up -d
```

### Parar Aplicação
```bash
docker-compose down
```

### Rebuild Completo
```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

### Ver Status
```bash
docker ps
```

---

## 📋 Logs

### Ver Logs (Todos)
```bash
docker-compose logs
```

### Ver Logs (Container Específico)
```bash
docker logs acesso-app1
docker logs acesso-app2
docker logs acesso-app3
docker logs acesso-nginx
docker logs acesso-postgres
```

### Seguir Logs em Tempo Real
```bash
docker logs -f acesso-app1
```

### Últimas 100 Linhas
```bash
docker logs --tail 100 acesso-app1
```

---

## 🧪 Testes

### Executar Todos os Testes
```bash
mvn test
```

### Executar Teste Específico
```bash
mvn test -Dtest=SolicitacaoServiceTest
```

### Gerar Relatório de Cobertura
```bash
mvn clean test jacoco:report
```

### Ver Relatório
```bash
# Abrir: target/site/jacoco/index.html
start target/site/jacoco/index.html
```

---

## 🔨 Build

### Compilar
```bash
mvn clean compile
```

### Empacotar (JAR)
```bash
mvn clean package
```

### Pular Testes
```bash
mvn clean package -DskipTests
```

### Limpar
```bash
mvn clean
```

---

## 🐳 Docker

### Listar Containers
```bash
docker ps
docker ps -a
```

### Listar Imagens
```bash
docker images
```

### Remover Container
```bash
docker rm acesso-app1
```

### Remover Imagem
```bash
docker rmi sistema-acesso-modulos_app1
```

### Limpar Sistema
```bash
docker system prune -a
```

### Ver Uso de Recursos
```bash
docker stats
```

### Acessar Container
```bash
docker exec -it acesso-app1 sh
```

---

## 🗄️ Banco de Dados

### Acessar PostgreSQL
```bash
docker exec -it acesso-postgres psql -U postgres -d acesso_modulos
```

### Comandos SQL Úteis
```sql
-- Listar tabelas
\dt

-- Descrever tabela
\d usuarios

-- Ver usuários
SELECT * FROM usuarios;

-- Ver módulos
SELECT * FROM modulos;

-- Ver solicitações
SELECT * FROM solicitacoes;

-- Sair
\q
```

### Backup do Banco
```bash
docker exec acesso-postgres pg_dump -U postgres acesso_modulos > backup.sql
```

### Restaurar Banco
```bash
docker exec -i acesso-postgres psql -U postgres acesso_modulos < backup.sql
```

---

## 🌐 Requisições HTTP

### Login
```bash
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ti@empresa.com","password":"senha123"}'
```

### Listar Módulos
```bash
curl -X GET http://localhost/api/modulos \
  -H "Authorization: Bearer {token}"
```

### Criar Solicitação
```bash
curl -X POST http://localhost/api/solicitacoes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{"moduloIds":[1,2],"justificativa":"Preciso acessar...","urgente":false}'
```

### Listar Solicitações
```bash
curl -X GET http://localhost/api/solicitacoes \
  -H "Authorization: Bearer {token}"
```

### Health Check
```bash
curl http://localhost/actuator/health
```

---

## 🔍 Diagnóstico

### Verificar Portas em Uso
```bash
# Windows
netstat -ano | findstr :80
netstat -ano | findstr :5432

# Linux/Mac
lsof -i :80
lsof -i :5432
```

### Testar Conectividade
```bash
# Ping entre containers
docker exec acesso-app1 ping postgres

# Curl interno
docker exec acesso-app1 wget -O- http://localhost:8080/actuator/health
```

### Ver Variáveis de Ambiente
```bash
docker exec acesso-app1 env
```

### Ver Processos no Container
```bash
docker exec acesso-app1 ps aux
```

---

## 📊 Monitoramento

### Ver Uso de CPU/Memória
```bash
docker stats
```

### Ver Logs de Erro
```bash
docker logs acesso-app1 2>&1 | grep ERROR
```

### Ver Logs de Warning
```bash
docker logs acesso-app1 2>&1 | grep WARN
```

### Contar Requisições
```bash
docker logs acesso-nginx | grep "POST /api/solicitacoes" | wc -l
```

---

## 🔧 Manutenção

### Reiniciar Container
```bash
docker restart acesso-app1
```

### Reiniciar Todos
```bash
docker-compose restart
```

### Atualizar Imagem
```bash
docker-compose pull
docker-compose up -d
```

### Ver Versões
```bash
docker --version
docker-compose --version
java -version
mvn --version
```

---

## 🎯 Atalhos Úteis

### Subir e Ver Logs
```bash
docker-compose up -d && docker-compose logs -f
```

### Rebuild e Subir
```bash
docker-compose down && docker-compose build && docker-compose up -d
```

### Limpar e Rebuild
```bash
docker-compose down -v && docker system prune -f && docker-compose up -d --build
```

### Testar e Ver Relatório
```bash
mvn clean test jacoco:report && start target/site/jacoco/index.html
```

---

## 📱 URLs Importantes

```
Swagger:        http://localhost/swagger-ui.html
API Docs:       http://localhost/api-docs
Health:         http://localhost/actuator/health
API Base:       http://localhost/api
```

---

## 👤 Credenciais de Teste

```
TI:         ti@empresa.com / senha123
Financeiro: financeiro@empresa.com / senha123
RH:         rh@empresa.com / senha123
Operações:  operacoes@empresa.com / senha123
```

---

## 🎨 Formatação de Saída

### JSON Pretty Print
```bash
curl http://localhost/api/modulos \
  -H "Authorization: Bearer {token}" | python -m json.tool
```

### Salvar Resposta
```bash
curl http://localhost/api/modulos \
  -H "Authorization: Bearer {token}" > modulos.json
```

---

## 🔐 Segurança

### Gerar Novo Secret JWT
```bash
# Windows PowerShell
[Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((New-Guid).ToString()))

# Linux/Mac
openssl rand -base64 32
```

### Ver Hash BCrypt de Senha
```bash
# Usar site: https://bcrypt-generator.com/
# Ou criar classe Java temporária
```

---

## 📦 Exportar/Importar

### Exportar Imagem Docker
```bash
docker save sistema-acesso-modulos_app1 > app.tar
```

### Importar Imagem Docker
```bash
docker load < app.tar
```

### Exportar Container
```bash
docker export acesso-app1 > container.tar
```

---

## 🚨 Emergência

### Parar Tudo Imediatamente
```bash
docker stop $(docker ps -q)
```

### Remover Tudo
```bash
docker-compose down -v
docker system prune -a -f
docker volume prune -f
```

### Reset Completo
```bash
# 1. Parar tudo
docker-compose down -v

# 2. Limpar
docker system prune -a -f

# 3. Rebuild
docker-compose build --no-cache

# 4. Subir
docker-compose up -d
```

---

## 💡 Dicas

### Alias Úteis (Bash/PowerShell)
```bash
# Adicionar ao .bashrc ou perfil do PowerShell
alias dcu='docker-compose up -d'
alias dcd='docker-compose down'
alias dcl='docker-compose logs -f'
alias dps='docker ps'
```

### Watch Logs
```bash
# Linux/Mac
watch -n 1 'docker logs --tail 20 acesso-app1'

# Windows PowerShell
while($true) { docker logs --tail 20 acesso-app1; sleep 1; clear }
```

---

**Salve este arquivo para referência rápida!** 📌
