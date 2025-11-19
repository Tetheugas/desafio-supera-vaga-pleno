@echo off
echo ========================================
echo  Teste Automatizado - Sistema de Acesso
echo ========================================
echo.

echo [1/5] Verificando Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ERRO: Docker nao encontrado!
    pause
    exit /b 1
)
echo OK - Docker instalado

echo.
echo [2/5] Subindo containers...
docker-compose up -d
if errorlevel 1 (
    echo ERRO: Falha ao subir containers!
    pause
    exit /b 1
)

echo.
echo [3/5] Aguardando containers ficarem prontos...
timeout /t 60 /nobreak >nul

echo.
echo [4/5] Verificando health dos containers...
docker ps --filter "name=acesso" --format "table {{.Names}}\t{{.Status}}"

echo.
echo [5/5] Testando endpoints...
echo.
echo Testando health check...
curl -s http://localhost/actuator/health
echo.
echo.

echo Testando login...
curl -s -X POST http://localhost/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"ti@empresa.com\",\"password\":\"senha123\"}"
echo.
echo.

echo ========================================
echo  Teste Concluido!
echo ========================================
echo.
echo Acesse o Swagger em: http://localhost/swagger-ui.html
echo.
echo Para parar os containers: docker-compose down
echo.
pause
