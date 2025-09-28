#!/usr/bin/env bash
# ------------------------------------------------------------
# 跨平台开发启动脚本（Unix/Linux/macOS）                     
# 作用：
#   1. 解析传入的 --port 参数（默认 8080）。
#   2. 后台轮询 Jetty 监听端口，待服务可访问后自动打开系统默认浏览器。
#   3. 前台执行 Maven Jetty 插件，确保输出实时显示。
# ------------------------------------------------------------
set -euo pipefail

PORT=8080
BROWSER_OPENER=""

# --------------------------- 参数解析 ---------------------------
while [[ $# -gt 0 ]]; do
  case "$1" in
    --port)
      if [[ -n "${2-}" ]]; then
        PORT="$2"
        shift 2
      else
        echo "错误：--port 需要指定端口号" >&2
        exit 1
      fi
      ;;
    *)
      echo "未知参数：$1" >&2
      exit 1
      ;;
  esac
done

# ---------------------- 选择合适的打开方式 ----------------------
if command -v xdg-open >/dev/null 2>&1; then
  BROWSER_OPENER="xdg-open"
elif command -v open >/dev/null 2>&1; then
  BROWSER_OPENER="open"
else
  echo "警告：未找到 xdg-open 或 open，无法自动打开浏览器。" >&2
fi

# ---------------------- 后台端口探测与打开 ----------------------
wait_for_port() {
  local url="http://localhost:${PORT}/"
  while true; do
    if curl -fsS "$url" >/dev/null 2>&1; then
      break
    elif command -v nc >/dev/null 2>&1 && nc -z localhost "$PORT" >/dev/null 2>&1; then
      break
    fi
    sleep 1
  done

  if [[ -n "$BROWSER_OPENER" ]]; then
    "$BROWSER_OPENER" "$url" >/dev/null 2>&1 &
  else
    echo "请手动访问 $url" >&2
  fi
}

wait_for_port &
WATCHER_PID=$!

cleanup() {
  if ps -p $WATCHER_PID >/dev/null 2>&1; then
    kill $WATCHER_PID >/dev/null 2>&1 || true
  fi
}
trap cleanup EXIT

# ---------------------- 前台运行 Jetty 服务 ----------------------
mvn -DskipTests -Djetty.port="${PORT}" jetty:run
