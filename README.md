# VibeCoding Assistant — AI 驱动的开发计划生成器

将自然语言的项目意图转化为结构化的开发计划，每个章节都包含**可直接复制粘贴**的 AI 编码提示词。

**前端**: Vue 3 + TypeScript + Vite  
**后端**: Spring Boot + 直接 HTTP 调用 OpenAI 兼容 API  
**AI**: 用户在界面中配置自己的 API Key（DeepSeek / OpenAI / 自定义兼容端点）

---

## 快速开始

### 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 21+ |
| Node.js | 18+ |

### 1. 启动后端

```bash
cd backend
./mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8080`（H2 内存数据库，无需额外配置）。

> **注意**：后端不再硬编码 API Key。AI 调用的 Key 由用户在界面中配置。

### 2. 启动前端

```bash
cd demoHelper
npm install
npm run dev
```

前端运行在 `http://localhost:5173`，Vite 自动代理 `/api` 到后端。

### 3. 配置 API Key

打开 `http://localhost:5173`，点击**右上角齿轮图标**进入设置：

1. 选择服务商（DeepSeek / OpenAI / 自定义）
2. 填入 API Key
3. 接口地址和模型会自动填充，也可手动修改
4. 点击保存

配置存储在浏览器 `localStorage` 中，不会上传到服务器。

---

## 使用方式

### 生成计划

1. 在输入框中描述你的项目需求（越详细越好）
2. 可选：填写项目名称、技术栈偏好、上传参考图片
3. 点击"生成开发计划"
4. 等待 AI 生成——页面会显示**实时进度步骤**和等待时间
5. 生成完成后可切换视图：卡片 / Markdown / JSON
6. 点击「复制全部提示词」即可逐节粘贴到 AI 编码工具中使用

### 修改计划

生成结果后，右侧会出现「调整计划」面板。输入修改意见（如"使用 PostgreSQL 替代 MySQL"），AI 会在**保留原始需求上下文**的基础上重新生成完整计划。

---

## 项目结构

```
demoHelper/
├── backend/                          # Spring Boot 后端
│   ├── pom.xml                       # Spring AI OpenAI starter
│   └── src/main/java/com/qzcy/backend/
│       ├── BackendApplication.java
│       ├── config/
│       │   ├── AiProperties.java     # 系统提示词
│       │   └── WebConfig.java        # CORS
│       ├── controller/
│       │   └── GenerateController.java  # REST + SSE 端点
│       ├── dto/                      # GenerateRequest / GenerateResponse 等
│       ├── entity/                   # GenerationHistory（JPA）
│       ├── exception/                # 全局异常处理
│       ├── repository/               # Spring Data JPA
│       └── service/
│           ├── GenerateService.java      # 业务编排（含 SSE 流式响应）
│           ├── DirectAiClient.java       # 直接 HTTP 调用 AI API
│           └── PlanOutputHelper.java     # JSON 容错解析 + 进度回调
│
├── demoHelper/                       # Vue 3 前端
│   ├── vite.config.ts                # Vite + API 代理
│   └── src/
│       ├── App.vue                   # 条件布局（首次居中 / 修改分栏）
│       ├── components/
│       │   ├── input/                # GuidedInput（输入表单）
│       │   ├── output/               # PlanDisplay / PlanSectionCard / ExportBar 等
│       │   ├── layout/               # AppHeader / ModifyPanel / SettingsModal
│       │   └── shared/               # LoadingSpinner / ErrorMessage / CopyButton / TabsBar
│       ├── composables/              # useGenerator / useApi / useSettings / useTheme 等
│       ├── types/                    # TypeScript 类型定义
│       └── utils/                    # 校验 / 格式化 / Markdown 渲染
│
└── README.md
```

---

## API 端点

### POST /api/generate

生成开发计划（阻塞式，返回 JSON）。

### POST /api/generate/stream

SSE 流式生成，实时推送进度事件：

```
event: progress
data: 0

event: progress
data: 2

event: progress
data: 3

event: result
data: {"id":"gen-...","title":"...","sections":[...]}
```

进度步骤：`0` 开始请求 → `2` 解析结果 → `3` 完成

### GET /api/history

获取历史记录列表。

### GET /api/history/{id}

获取单条历史记录详情。

### GET /api/health

健康检查。

---

## AI 兼容性

后端通过 `DirectAiClient` 直接调用 OpenAI 兼容的 `/v1/chat/completions` 端点，因此支持：

- **DeepSeek** — `https://api.deepseek.com`（默认）
- **OpenAI** — `https://api.openai.com`
- **任意 OpenAI 兼容代理** — 在设置中填写自定义接口地址即可

所有 AI 调用统一使用以下参数：

```json
{
  "model": "deepseek-v4-flash",
  "temperature": 0.7,
  "messages": [
    { "role": "system", "content": "<系统提示词>" },
    { "role": "user", "content": "<用户需求>" }
  ]
}
```

---

## 配置参考

### 服务器环境变量（可选）

如果用户未在界面配置 API Key，后端会回退到以下环境变量：

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `AI_API_KEY` | API Key | （空） |
| `AI_BASE_URL` | 接口地址 | `https://api.deepseek.com` |
| `AI_MODEL` | 模型 | `deepseek-v4-flash` |

### 数据库

开发环境默认使用 H2 内存数据库。生产环境在 `application-prod.properties` 中配置 MySQL：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/demohelper
spring.datasource.username=root
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update
```

### 容错 JSON 解析

`PlanOutputHelper.parseLenient()` 自动处理 LLM 响应中常见的格式问题：
- Markdown 代码块包装（```` ```json ... ``` ````）
- 未转义的控制字符
- 尾部逗号
- 无引号的属性名
- 响应中混杂的解释文本（自动提取 `{...}` 部分）

---

## 生产部署

### 后端

```bash
cd backend
./mvnw package -DskipTests
java -jar target/backend-*.jar --spring.profiles.active=prod
```

### 前端

```bash
cd demoHelper
npm run build       # 输出到 dist/
```

将 `dist/` 部署到 Nginx，或放入 Spring Boot 的 `src/main/resources/static/`。
