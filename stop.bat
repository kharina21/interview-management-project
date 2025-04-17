@echo off
echo Stopping Interview Management System servers...

:: Kill backend and frontend processes
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM node.exe >nul 2>&1

echo Servers have been stopped.
timeout /t 2 >nul