@echo off
rem Sobe a API numa janela propria e abre o site no Chrome quando o servidor responder.
rem Para desligar: Ctrl+C (ou fechar) a janela "API Calculadora Penal".

rem %~dp0 = pasta deste arquivo; funciona mesmo aberto com dois cliques de outro lugar
cd /d "%~dp0"

rem "start" abre outra janela; "cmd /k" mantem ela aberta se der erro, para dar tempo de ler
start "API Calculadora Penal" cmd /k gradlew.bat :api:run

echo Aguardando a API subir (a primeira vez demora, porque gera o site)...
set /a tentativas=0

:espera
timeout /t 2 /nobreak >nul
rem curl falha enquanto ninguem responde na porta; quando responde, segue para abrir
curl -s -o nul http://127.0.0.1:8080 && goto abrir
set /a tentativas+=1
if %tentativas% lss 90 goto espera

echo A API nao respondeu em 3 minutos. Veja a mensagem na outra janela.
pause
exit /b 1

:abrir
start chrome http://127.0.0.1:8080