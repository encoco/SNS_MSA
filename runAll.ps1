#powershell -ExecutionPolicy Bypass -File .\runAll.ps1

$services = @(
    @{ Name = "🔥 Eureka";     Command = "./gradlew :eureka-server:bootRun" },
    @{ Name = "🚀 User";       Command = "./gradlew :user-service:bootRun" },
    @{ Name = "💬 Chat";       Command = "./gradlew :chat-service:bootRun" },
    @{ Name = "📋 Board";      Command = "./gradlew :board-service:bootRun" },
    @{ Name = "🌐 Gateway";    Command = "./gradlew :gateway-service:bootRun" },
    @{ Name = "🔔 Alarm";      Command = "./gradlew :Alarm-service:bootRun" },
    @{ Name = "🧃 Kafka";      Command = "./kafka/bin/windows/kafka-server-start.bat ./kafka/config/server.properties" }
)

$processIds = @()

foreach ($svc in $services) {
    $title = $svc.Name
    $cmd = $svc.Command

    $args = "-NoExit -Command `$host.UI.RawUI.WindowTitle = '$title'; Write-Host '▶ $title 실행 중...' -ForegroundColor Green; $cmd"

    $proc = Start-Process powershell -PassThru -WindowStyle Normal -ArgumentList $args
    $processIds += $proc.Id
}

$processIds | Out-File -Encoding ASCII ".\services.pid"
Write-Host "`n 모든 서비스 실행 완료. 종료하려면 'stop.ps1' 실행"