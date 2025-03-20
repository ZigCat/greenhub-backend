@echo off
set DOCKER_BUILDKIT=1
set COMPOSE_DOCKER_CLI_BUILD=1
cd /d %~dp0
docker-compose down
docker-compose up --build -d
echo Project started!
pause