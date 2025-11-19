@echo off
echo Executando testes e gerando relatorio de cobertura...
call mvn clean test jacoco:report
echo.
echo Relatorio gerado em: target\site\jacoco\index.html
echo.
pause
