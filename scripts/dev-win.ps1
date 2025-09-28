# ------------------------------------------------------------
# Windows PowerShell 开发启动脚本
# 功能：
#   1. 解析可选参数 -Port，默认监听 8080。
#   2. 后台循环调用 Test-NetConnection 探测端口是否可用。
#   3. 一旦端口可访问，自动用系统默认浏览器打开站点首页。
#   4. 主线程启动 Maven Jetty 插件并实时输出日志。
# ------------------------------------------------------------
param(
    [int]$Port = 8080
)

$ErrorActionPreference = 'Stop'
$url = "http://localhost:$Port/"

# -------------------- 后台端口探测与浏览器打开 --------------------
$watcherScript = {
    param($innerPort, $targetUrl)
    while ($true) {
        if (Test-NetConnection -ComputerName 'localhost' -Port $innerPort -InformationLevel Quiet) {
            Start-Process $targetUrl
            break
        }
        Start-Sleep -Seconds 1
    }
}

$job = Start-Job -ScriptBlock $watcherScript -ArgumentList $Port, $url

try {
    # ----------------------- 前台运行 Jetty -----------------------
    mvn -DskipTests -Djetty.port=$Port jetty:run
}
finally {
    if ($null -ne $job) {
        try {
            Receive-Job -Job $job -Wait -AutoRemoveJob | Out-Null
        } catch {
            Remove-Job -Job $job -Force -ErrorAction SilentlyContinue
        }
    }
}
