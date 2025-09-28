#!/usr/bin/env bash
# 自动等待 Jetty 端口并在就绪后打开浏览器的辅助脚本。 #
set -euo pipefail

port="8080" # 默认端口
jetty_requested=false # 标记是否检测到 jetty:run
args=("$@") # 保存所有参数供后续解析

for ((i=0; i<${#args[@]}; i++)); do
  arg="${args[i]}" # 当前参数
  case "$arg" in
    -Djetty.port=*)
      candidate="${arg#*=}" # 取得等号后的端口
      ;;
    -Djetty.port)
      if (( i + 1 < ${#args[@]} )); then
        candidate="${args[i+1]}" # 取得紧跟其后的端口值
      else
        candidate=""
      fi
      ;;
    *)
      candidate=""
      ;;
  esac

  if [[ -n "${candidate:-}" ]]; then
    candidate="${candidate%/}" # 移除可能的结尾斜杠
    candidate="${candidate%"}" # 移除可能的右引号
    candidate="${candidate#"}" # 移除可能的左引号
    candidate="${candidate%'}" # 移除单引号结尾
    candidate="${candidate#'}" # 移除单引号开头
    if [[ "$candidate" =~ ^[0-9]+$ ]]; then
      port="$candidate" # 只有纯数字才接受
    fi
  fi

  if [[ "$arg" == *"jetty:run"* ]]; then
    jetty_requested=true # 捕获包含 jetty:run 的参数
  fi
done

if [[ "$jetty_requested" != true ]]; then
  exit 0 # 未触发 Jetty 运行时直接退出
fi

url="http://localhost:${port}/" # 拼接完整访问地址

if command -v xdg-open >/dev/null 2>&1; then
  opener="xdg-open" # Linux 打开默认浏览器
elif command -v open >/dev/null 2>&1; then
  opener="open" # macOS 打开默认浏览器
else
  echo "未检测到 xdg-open 或 open，放弃自动打开浏览器。" >&2
  exit 0
fi

has_curl=false # 记录 curl 是否可用
has_nc=false # 记录 nc 是否可用
if command -v curl >/dev/null 2>&1; then
  has_curl=true # curl 可用
fi
if command -v nc >/dev/null 2>&1; then
  has_nc=true # nc 可用
fi

if [[ "$has_curl" != true && "$has_nc" != true ]]; then
  echo "缺少 curl 与 nc，无法探测端口，跳过自动打开。" >&2
  exit 0
fi

while true; do
  success=false # 标记当前循环是否检测成功
  if [[ "$has_curl" == true ]]; then
    if curl -fsS "$url" >/dev/null 2>&1; then
      success=true # HTTP 请求成功
    fi
  fi
  if [[ "$success" != true && "$has_nc" == true ]]; then
    if nc -z localhost "$port" >/dev/null 2>&1; then
      success=true # 端口监听成功
    fi
  fi

  if [[ "$success" == true ]]; then
    "$opener" "$url" >/dev/null 2>&1 & # 浏览器后台打开
    break
  fi
  sleep 1 # 每秒重试一次
done

