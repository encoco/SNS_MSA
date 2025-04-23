#powershell -ExecutionPolicy Bypass -File ./stop.ps1
Write-Host "모든 서비스 종료 중..." -ForegroundColor Red

# 종료할 프로세스 이름 목록
$services = @(
    "java",
    "gradlew",
    "kafka-server-start",
    "Alarm-Service",
    "gateway-service",
    "board-service",
    "user-service",
    "chat-service",
    "eureka-server"
)

foreach ($svc in $services) {
    Get-Process | Where-Object { $_.ProcessName -like "*$svc*" } | ForEach-Object {
        try {
            Stop-Process -Id $_.Id -Force
            Write-Host "종료됨 → $($_.ProcessName) (PID: $($_.Id))" -ForegroundColor Green
        } catch {
            Write-Host "종료 실패 → $($_.ProcessName) (PID: $($_.Id))" -ForegroundColor Yellow
        }
    }
}

Write-Host "`n 모든 서비스 종료 완료!" -ForegroundColor Cyan
pause