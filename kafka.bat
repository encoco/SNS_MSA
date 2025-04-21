@echo off
echo ⚡ Kafka (KRaft 모드) 실행 중...
cd /d %~dp0bin\windows
start "KAFKA" cmd /k kafka-server-start.bat ..\..\config\kraft\server.properties