@echo off
cd /d %~dp0
docker-compose down
docker-compose up --build -d
echo Project started!
pause