# VibeCoding Assistant — AI 驱动的开发计划生成器

将自然语言的项目意图转化为结构化的开发计划，包含可直接复制粘贴的 AI 编码提示词。

**前端**: Vue 3 + TypeScript + Vite  
**后端**: Spring Boot 4.0 + Spring AI 2.0  
**AI**: Anthropic Claude / OpenAI / DeepSeek（可切换）

---

## 快速开始

### 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 21+ |
| Node.js | 18+ |
| Maven | 3.9+（已内置 wrapper） |

### 1. 启动后端

```bash
cd backend

# 设置 API Key（三选一）
set DEEPSEEK_API_KEY=sk-your-key-here        # Windows
# export DEEPSEEK_API_KEY=sk-your-key-here   # Linux/macOS

# 启动（默认 deepseek provider，H2 内存库）
./mvnw spring-boot:run
```

后端运行在 `http://localhost:8080`。

### 2. 启动前端

```bash
cd demoHelper
npm install
npm run dev
```

前端运行在 `http://localhost:5173`，Vite 自动将 `/api` 请求代理到后端。

---

## AI Provider 切换

编辑 `backend/src/main/resources/application.properties`：

### DeepSeek（默认）

```properties
ai.provider=deepseek
spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY:sk-...}
spring.ai.deepseek.base-url=${DEEPSEEK_BASE_URL:https://api.deepseek.com}
spring.ai.deepseek.chat.options.model=deepseek-chat
```

### Anthropic Claude

```properties
ai.provider=anthropic
spring.ai.anthropic.api-key=${ANTHROPIC_API_KEY:sk-ant-...}
spring.ai.anthropic.base-url=${ANTHROPIC_BASE_URL:https://api.anthropic.com}
spring.ai.anthropic.chat.options.model=claude-sonnet-4-6
```

### OpenAI

```properties
ai.provider=openai
spring.ai.openai.api-key=${OPENAI_API_KEY:sk-...}
spring.ai.openai.base-url=${OPENAI_BASE_URL:https://api.openai.com}
spring.ai.openai.chat.options.model=gpt-4o
```

### 使用代理 / 兼容 API

修改 `base-url` 即可对接任意兼容端点，例如 Anthropic 格式的代理：

```properties
ai.provider=anthropic
spring.ai.anthropic.base-url=https://your-proxy.com
```

### 调整超时

如果模型响应较慢（尤其是大项目计划），可以增加超时：

```properties
spring.ai.deepseek.chat.options.timeout=600s   # 10 分钟
```

---

## 项目结构

```
demoHelper/
├── backend/                          # Spring Boot 后端
│   ├── pom.xml                       # 依赖（Spring AI starter）
│   └── src/main/java/com/qzcy/backend/
│       ├── BackendApplication.java
│       ├── config/
│       │   ├── AiProperties.java     # Provider 选择 & 系统提示词
│       │   └── WebConfig.java        # CORS
│       ├── controller/
│       │   └── GenerateController.java  # REST API
│       ├── dto/                      # 请求/响应 DTO
│       ├── entity/                   # JPA 实体
│       ├── exception/                # 全局异常处理
│       ├── repository/               # Spring Data JPA
│       └── service/
│           ├── PlanGenerator.java            # Provider 抽象接口
│           ├── AnthropicPlanGenerator.java   # Anthropic 实现
│           ├── OpenAiPlanGenerator.java      # OpenAI 实现
│           ├── DeepSeekPlanGenerator.java    # DeepSeek 实现
│           ├── GenerateService.java          # 业务编排
│           └── PlanOutputHelper.java         # JSON 容错解析 + Tool 定义
│
├── demoHelper/                       # Vue 3 前端
│   ├── vite.config.ts                # Vite + API 代理配置
│   └── src/
│       ├── App.vue                   # 主布局
│       ├── components/
│       │   ├── input/                # 输入表单
│       │   ├── output/               # 结果展示（卡片/JSON/Markdown）
│       │   ├── layout/               # 布局组件
│       │   └── shared/               # 通用组件（错误/加载/复制按钮）
│       ├── composables/              # useGenerator, useApi, useExport
│       ├── types/                    # TypeScript 类型定义
│       └── utils/                    # 校验、格式化、Markdown 渲染
│
└── README.md
```

---

## API 端点

### POST /api/generate

生成开发计划。

**请求体**:
```json
{
  "intentText": "我想做一个个人博客，支持 Markdown 编辑和评论功能",
  "imageBase64": null
}
```

**响应**:
```json
{
  "id": "gen-a1b2c3d4",
  "title": "个人博客全栈应用",
  "description": "使用 Next.js + Markdown + Supabase 构建功能完整的个人博客...",
  "sections": [
    {
      "id": "sec-1",
      "order": 1,
      "title": "项目初始化与开发环境搭建",
      "description": "创建 Next.js 项目，安装依赖，配置 Tailwind CSS...",
      "prompt": "我需要你帮我创建一个全新的 Next.js 项目...",
      "estimatedHours": 2,
      "difficulty": "beginner"
    }
  ],
  "metadata": {
    "totalSections": 6,
    "estimatedTotalHours": 20,
    "difficulty": "intermediate",
    "generatedAt": "2026-06-08T10:30:00Z"
  }
}
```

### GET /api/history

获取历史记录列表。

### GET /api/history/{id}

获取单条历史记录详情（含完整 JSON）。

### GET /api/health

健康检查。

---

## 生产部署

### 后端

```bash
cd backend
# 使用 MySQL 替代 H2
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

编辑 `backend/src/main/resources/application-prod.properties` 配置 MySQL 连接。

### 前端

```bash
cd demoHelper
npm run build       # 输出到 dist/
```

可将 `dist/` 部署到 Nginx，或放入 Spring Boot 的 `src/main/resources/static/` 由后端直接托管。

---

## 开发说明

### 添加新的 AI Provider

1. 在 `pom.xml` 中引入对应的 Spring AI starter
2. 创建 `XxxPlanGenerator implements PlanGenerator`
3. 在 `application.properties` 中添加配置项
4. 在 `AiProperties.Provider` 枚举中注册

所有 Provider 共享同一套 `PlanOutputHelper.PlanOutputToolCallback`，通过 tool/function calling 保证结构化输出。

### 容错 JSON 解析

`PlanOutputHelper.parseLenient()` 自动处理 LLM 响应中常见的格式问题：
- Markdown 代码块包装（```` ```json ... ``` ````）
- 未转义的控制字符
- 尾部逗号
- 无引号的属性名
