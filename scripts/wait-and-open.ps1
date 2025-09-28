param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$ArgsFromWrapper
)

# 默认端口设为 8080，后续会根据参数覆盖
$port = 8080
# 标记是否真的包含 jetty:run 命令
$jettyRequested = $false

for ($i = 0; $i -lt $ArgsFromWrapper.Length; $i++) {
    $arg = $ArgsFromWrapper[$i]
    if ($arg -match 'jetty:run') {
        $jettyRequested = $true
    }
    if ($arg -match '^-Djetty\.port=(.+)$') {
        $candidate = $Matches[1].Trim('"')
    } elseif ($arg -eq '-Djetty.port' -and $i + 1 -lt $ArgsFromWrapper.Length) {
        $candidate = $ArgsFromWrapper[$i + 1].Trim('"')
    } else {
        $candidate = $null
    }

    if ($candidate -and $candidate -match '^[0-9]+$') {
        $port = [int]$candidate # 只接受纯数字端口
    }
}

if (-not $jettyRequested) {
    return # 未执行 jetty:run 时直接退出
}

$url = "http://localhost:$port/" # 构造监听地址

$scriptBlock = {
    param($probeUrl, $probePort)
    while ($true) {
        try {
            $response = Invoke-WebRequest -Uri $probeUrl -UseBasicParsing -TimeoutSec 2
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 600) {
                Start-Process $probeUrl
                break
            }
        } catch {
            try {
                if (Test-NetConnection -ComputerName 'localhost' -Port $probePort -InformationLevel Quiet) {
                    Start-Process $probeUrl # 端口开放但无 HTTP 时同样打开
                    break
                }
            } catch {
                # 忽略临时网络异常继续重试
            }
        }
        Start-Sleep -Seconds 1 # 每秒重试一次
    }
}

try {
    Start-Job -ScriptBlock $scriptBlock -ArgumentList $url, $port | Out-Null # 后台作业方式
} catch {
    # 某些精简版 PowerShell 不支持 Start-Job，这里退回隐藏窗口方式
    Start-Process powershell -ArgumentList '-NoProfile', '-WindowStyle', 'Hidden', "-Command", $scriptBlock.ToString()
}
