# Themeleaf Demo

一键运行：`./mvnw -DskipTests jetty:run`，脚本会在 Jetty 服务启动并监听目标端口后自动通过系统默认浏览器打开 `http://localhost:8080/`。

使用 Java 8、Servlet 3.1 与 Thymeleaf 3 构建的最小演示站点，提供课程列表与详情浏览。

## 程序运行顺序

1. 浏览器发起请求。<!-- 步骤一：浏览器 -->
2. Jetty 容器接收请求。<!-- 步骤二：Jetty -->
3. 根据 URL 将请求映射到对应 Servlet。<!-- 步骤三：Servlet 映射 -->
4. Servlet 读取查询参数并从内存仓库检索数据。<!-- 步骤四：参数与仓库 -->
5. Servlet 将模型数据放入 Thymeleaf 上下文。<!-- 步骤五：构建上下文 -->
6. Thymeleaf 渲染模板生成 HTML。<!-- 步骤六：模板渲染 -->
7. Jetty 将渲染后的 HTML 返回给浏览器。<!-- 步骤七：返回响应 -->
8. 浏览器展示最终页面。<!-- 步骤八：展示 -->

针对关键页面的判断流程：

- **课程列表**：Servlet 解析 `keyword`/`page`/`size`，过滤标题或简介包含关键字的课程，执行简单分页，将结果及统计信息写入上下文后渲染 `courses/list` 模板。
- **课程详情**：Servlet 解析 `id`，查询单个课程并根据是否存在展示不同提示，模板通过 `th:switch` 显示级别文案，并根据 `minAge` 与 `requireRole` 分别显示成人/管理员提示。

## 运行与调试

### Unix / macOS

```bash
./mvnw -DskipTests jetty:run
```

### Windows

```powershell
./mvnw.cmd -DskipTests jetty:run
```

无论在哪个平台，`mvnw` / `mvnw.cmd` 都会在检测到 `jetty:run` 时调用 `scripts/wait-and-open.sh` 或 `scripts/wait-and-open.ps1`。脚本会解析命令行参数中的 `-Djetty.port`（若未提供则默认为 8080），持续探测端口是否开放，并在成功后自动打开浏览器访问该端口主页。脚本内部包含容错逻辑：Unix 系统通过 `curl`/`nc` 轮询，Windows 则使用 `Test-NetConnection` 与 `Start-Job` 后台运行。

## 修改端口

- **命令行覆盖**：运行时追加 `-Djetty.port=3000` 即可将监听端口改为 3000，自动打开脚本也会识别新端口。
- **Jetty 插件配置**：在 `pom.xml` 的 `jetty-maven-plugin` 配置块内修改 `<httpConnector><port>...</port></httpConnector>` 后重新运行即可生效。

## 验证路径


- http://localhost:8080/
- http://localhost:8080/tags
- http://localhost:8080/courses
- http://localhost:8080/courses/detail?id=1

## 功能

- 使用内存仓库提供多条课程数据（含管理员专享、不同年龄限制）。
- 课程列表支持关键字搜索与分页参数控制。
- 课程详情提供成人向与管理员提示，并在级别间切换文案。
- Header 片段演示 Thymeleaf 模板复用。

## 一键运行脚本（自动打开浏览器）

- Windows：`powershell -ExecutionPolicy Bypass -File scripts/dev-win.ps1 -Port 8080`
- Linux/macOS：`bash scripts/dev-unix.sh --port 8080`

脚本会将端口参数同时传递给 Jetty 插件与端口监听逻辑，因此如果改为其他端口（例如 `-Port 3000` 或 `--port 3000`），浏览器自动打开的 URL 也会同步切换至 `http://localhost:3000/`。

## 程序运行顺序（脚本执行视角）

1. 浏览器发起请求并访问脚本打开的地址。<!-- 浏览器起点 -->
2. Jetty 容器接收请求并托管 Servlet。<!-- Jetty 容器 -->
3. 容器根据 URL 映射到对应的 Servlet。<!-- Servlet 匹配 -->
4. Servlet 读取查询参数并从内存仓库获取数据。<!-- 参数与仓库 -->
5. Servlet 将数据模型写入 Thymeleaf 上下文。<!-- 模型上下文 -->
6. Thymeleaf 渲染模板生成最终 HTML。<!-- 模板渲染 -->
7. Jetty 将渲染后的内容返回给浏览器。<!-- 返回响应 -->
8. 浏览器展示页面，并可继续发起新的交互请求。<!-- 展示结果 -->

### 列表页判断逻辑
- 解析 `keyword`、`page`、`size` 参数，根据标题或简介过滤课程。
- 计算分页信息，将课程集合、总数与分页参数存入上下文后渲染 `courses/list` 模板。
- 模板基于 `minAge` 与 `requireRole` 条件展示成人或管理员提示。

### 详情页判断逻辑
- 读取 `id` 参数并查询单个课程。
- 若查询不到课程，则在模板内提示“未找到”并提供返回列表的链接。
- 若课程存在，则展示标题、级别、简介，并用 `th:switch` 显示 BEGINNER/ADVANCED 的提示，同时根据 `minAge` 控制成人向标签。

