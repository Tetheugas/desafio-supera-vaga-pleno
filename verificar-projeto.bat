@echo off
echo ========================================
echo  Verificacao do Projeto
echo ========================================
echo.

echo [1/6] Verificando estrutura de arquivos...
echo.

set ARQUIVOS_OBRIGATORIOS=pom.xml Dockerfile docker-compose.yml nginx.conf README.md .gitignore
set ARQUIVOS_FALTANDO=0

for %%f in (%ARQUIVOS_OBRIGATORIOS%) do (
    if exist %%f (
        echo [OK] %%f
    ) else (
        echo [ERRO] %%f NAO ENCONTRADO!
        set /a ARQUIVOS_FALTANDO+=1
    )
)

echo.
echo [2/6] Verificando documentacao...
echo.

set DOCS=SUMARIO.md INICIO_RAPIDO.md ARQUITETURA.md DECISOES_TECNICAS.md EXEMPLOS_REQUISICOES.md GUIA_IA.md CHECKLIST_ENTREGA.md USO_IA.md APRESENTACAO.md ROADMAP.md

for %%d in (%DOCS%) do (
    if exist %%d (
        echo [OK] %%d
    ) else (
        echo [AVISO] %%d nao encontrado
    )
)

echo.
echo [3/6] Verificando estrutura de codigo...
echo.

if exist src\main\java\com\empresa\acesso (
    echo [OK] Estrutura de pacotes Java
) else (
    echo [ERRO] Estrutura de pacotes nao encontrada!
    set /a ARQUIVOS_FALTANDO+=1
)

if exist src\main\resources\application.yml (
    echo [OK] application.yml
) else (
    echo [ERRO] application.yml nao encontrado!
    set /a ARQUIVOS_FALTANDO+=1
)

if exist src\main\resources\db\migration (
    echo [OK] Migrations Flyway
) else (
    echo [ERRO] Migrations nao encontradas!
    set /a ARQUIVOS_FALTANDO+=1
)

echo.
echo [4/6] Verificando testes...
echo.

if exist src\test\java\com\empresa\acesso (
    echo [OK] Estrutura de testes
) else (
    echo [ERRO] Testes nao encontrados!
    set /a ARQUIVOS_FALTANDO+=1
)

echo.
echo [5/6] Verificando Docker...
echo.

docker --version >nul 2>&1
if errorlevel 1 (
    echo [AVISO] Docker nao encontrado ou nao esta rodando
) else (
    echo [OK] Docker instalado
)

docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [AVISO] Docker Compose nao encontrado
) else (
    echo [OK] Docker Compose instalado
)

echo.
echo [6/6] Verificando Maven...
echo.

mvn --version >nul 2>&1
if errorlevel 1 (
    echo [AVISO] Maven nao encontrado (necessario para testes locais)
) else (
    echo [OK] Maven instalado
)

echo.
echo ========================================
echo  Resumo da Verificacao
echo ========================================
echo.

if %ARQUIVOS_FALTANDO% EQU 0 (
    echo [OK] Todos os arquivos obrigatorios presentes!
    echo.
    echo Proximo passo:
    echo 1. Execute: docker-compose up -d
    echo 2. Aguarde ~60 segundos
    echo 3. Acesse: http://localhost/swagger-ui.html
) else (
    echo [ERRO] %ARQUIVOS_FALTANDO% arquivo(s) obrigatorio(s) faltando!
    echo.
    echo Verifique a estrutura do projeto.
)

echo.
pause
