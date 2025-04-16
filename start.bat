@echo off
echo Shutting down any previously running servers...

:: Kill previous backend and frontend processes if running
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM node.exe >nul 2>&1

timeout /t 2 >nul

echo Starting Interview Management System...

:: Start backend in detached mode
start /B "Backend Server" cmd /c "cd itviec-backend && call mvnw.cmd spring-boot:run"

:: Start frontend in detached mode
start /B "Frontend Server" cmd /c "cd itviec-frontend && npm run dev"

echo Servers are starting up...
echo Backend will be available at http://localhost:8080
echo Frontend will be available at http://localhost:5173
echo.
echo Servers are running in the background. You can close this window.
echo To stop the servers, run stop.bat
