@echo off
:: Garante que o script rode na pasta onde ele está (src)
cd /d "%~dp0"

echo --- DIAGNOSTICO ---
echo Estou na pasta: %CD%
echo Verificando arquivos...

if exist Main.java ( echo [OK] Main.java encontrado ) else ( echo [ERRO] Main.java NAO esta aqui! & pause & exit )
if exist backend\FSSimulador.java ( echo [OK] Backend encontrado ) else ( echo [ERRO] Pasta backend nao encontrada! & pause & exit )

echo.
echo --- 1. LIMPANDO BAGUNCA (.class antigos) ---
del /s /q *.class >nul 2>&1

echo.
echo --- 2. COMPILANDO (Forcando lista de arquivos) ---
:: Aqui listamos TODOS os arquivos explicitamente para nao ter erro de wildcard
javac -cp . Main.java backend/FSSimulador.java backend/FSNode.java backend/Arquivo.java backend/Diretorio.java frontend/Journal.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERRO FATAL] A compilacao falhou. Leia as mensagens acima.
    echo DICA: Verifique se Main.java ainda tem "package frontend;" na primeira linha. Se tiver, apague.
    pause
    exit /b
)

echo.
echo --- 3. EXECUTANDO ---
java -cp . Main
pause