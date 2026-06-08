import type { GenerateResponse } from '../types'

export const sampleResponse: GenerateResponse = {
  id: 'demo-001',
  title: 'React + Firebase 全栈待办事项应用',
  description: '使用 React 18、TypeScript、Firebase Auth + Firestore 构建功能完整的待办事项应用。',
  sections: [
    {
      id: 'sec-1',
      order: 1,
      title: '项目初始化与开发环境搭建',
      description: '使用 Vite 创建 React + TypeScript 项目，安装所有必要依赖，配置 Tailwind CSS 和 ESLint。',
      estimatedHours: 2,
      difficulty: 'beginner',
      prompt: `我需要你帮我创建一个全新的 React + TypeScript 项目。请按以下步骤执行：

1. 使用 Vite 创建项目，选择 React + TypeScript 模板
2. 安装以下依赖：
   - tailwindcss @tailwindcss/vite（用于样式）
   - react-router-dom（路由管理）
   - zustand（状态管理）
   - react-hot-toast（通知提示）
   - firebase（后端服务）
3. 配置 Tailwind CSS，在 src/index.css 中添加 @import "tailwindcss"
4. 配置 ESLint 和 Prettier
5. 在 vite.config.ts 中配置 tailwindcss 插件和路径别名 (@/ -> src/)
6. 创建基础目录结构：src/components, src/pages, src/hooks, src/stores, src/lib, src/types

请生成所有配置文件的完整代码，并确保项目可以直接 npm run dev 启动。`,
    },
    {
      id: 'sec-2',
      order: 2,
      title: 'Firebase 配置与用户认证系统',
      description: '配置 Firebase 项目，实现邮箱密码登录、Google 第三方登录、用户注册和登出功能。',
      estimatedHours: 4,
      difficulty: 'intermediate',
      prompt: `请为一个 React + TypeScript 项目实现完整的 Firebase 认证系统。

**Firebase 配置：**
- 创建 src/lib/firebase.ts，从环境变量读取 Firebase 配置
- 初始化 Firebase App、Auth、Firestore
- 导出 auth 和 db 实例

**认证系统：**
- 创建 src/contexts/AuthContext.tsx，使用 React Context 管理认证状态
- 实现 useAuth hook，包含：
  - user 状态（User | null）
  - loading 状态
  - signUp(email, password) 注册方法
  - signIn(email, password) 登录方法
  - signInWithGoogle() Google 登录方法
  - signOut() 登出方法
- 使用 onAuthStateChanged 监听认证状态变化
- 创建 ProtectedRoute 组件，未登录时重定向到 /login

**登录/注册页面：**
- 创建 src/pages/LoginPage.tsx
- 居中卡片布局，包含应用标题
- 邮箱和密码输入框，带表单验证（邮箱格式、密码最少 8 位）
- "使用 Google 登录" 按钮
- 登录/注册模式切换链接
- 显示 Firebase 错误信息（中文提示）
- 提交按钮显示加载动画

**环境变量 (.env.example)：**
列出所有需要的环境变量及其说明。

请生成完整、可运行的代码，包含所有 TypeScript 类型定义和错误处理。`,
    },
    {
      id: 'sec-3',
      order: 3,
      title: '待办事项数据模型与 Firestore CRUD',
      description: '定义 Todo 数据模型，实现 Firestore 的增删改查操作和实时数据同步。',
      estimatedHours: 4,
      difficulty: 'intermediate',
      prompt: `请为一个 React + Firebase 项目实现完整的待办事项数据层。

**数据模型 (src/types/todo.ts)：**
- Todo 接口：id, userId, title, completed, priority(low/medium/high), createdAt, updatedAt
- TodoInput 接口：title, priority（创建/更新时的输入类型）

**Firestore 服务 (src/lib/todoService.ts)：**
实现以下函数：
- getTodos(userId): 使用 onSnapshot 实时监听，返回取消订阅函数
- addTodo(userId, input): 添加文档，使用 serverTimestamp()
- updateTodo(todoId, data): 部分更新（使用 Firestore 的 merge 选项）
- deleteTodo(todoId): 软删除（添加 deletedAt 字段而非真的删除）
- toggleTodo(todoId, completed): 原子化切换完成状态
- 查询条件：按 userId 过滤，按 createdAt 降序排列

**Zustand Store (src/stores/todoStore.ts)：**
- todos, isLoading, error 状态
- fetchTodos(userId): 调用 Firestore 实时监听
- addTodo, toggleTodo, deleteTodo, updateTodo: 先乐观更新 UI，再同步 Firestore
- Firestore 操作失败时回滚 UI 状态
- 计算属性：activeTodos, completedTodos

请确保所有代码包含完整的 TypeScript 类型、错误处理和加载状态管理。`,
    },
    {
      id: 'sec-4',
      order: 4,
      title: '待办事项 UI 组件',
      description: '构建待办列表、待办项、添加表单、筛选器和搜索等完整 UI 组件。',
      estimatedHours: 5,
      difficulty: 'intermediate',
      prompt: `请为一个 React + TypeScript + Tailwind CSS 项目构建完整的待办事项 UI。

**TodoList 组件 (src/components/TodoList.tsx)：**
- 从 todoStore 获取待办事项列表
- 空状态：显示插图和"还没有待办事项，创建第一个吧"文字
- 加载状态：骨架屏动画
- 错误状态：显示错误信息及重试按钮

**TodoItem 组件 (src/components/TodoItem.tsx)：**
- 带动画的圆形复选框（勾选时显示对勾动画）
- 待办标题（双击进入编辑模式，Enter 保存，Esc 取消）
- 优先级彩色标签（低=绿色，中=黄色，高=红色）
- 删除按钮（悬停时显示，带确认动画）
- 已完成项显示删除线和灰色文字
- 使用 framer-motion 做进入/退出动画

**AddTodoForm 组件 (src/components/AddTodoForm.tsx)：**
- 文本输入框（自动聚焦）+ 优先级下拉选择
- 按 Enter 提交，空值时显示校验提示
- 提交后清空输入框

**FilterBar 组件 (src/components/FilterBar.tsx)：**
- 选项卡：全部 | 未完成 | 已完成
- 优先级筛选下拉菜单
- 搜索输入框（300ms 防抖）
- "清除已完成" 按钮（仅在有已完成项时显示）

所有组件需要完整的 TypeScript 类型、无障碍支持（aria-label、role、键盘导航）和响应式设计。`,
    },
    {
      id: 'sec-5',
      order: 5,
      title: '应用路由与页面组装',
      description: '配置 React Router，组装所有页面和组件，实现完整的应用流程。',
      estimatedHours: 2,
      difficulty: 'beginner',
      prompt: `请为一个 React + TypeScript 项目配置路由并组装应用。

**路由配置 (src/App.tsx)：**
- 使用 react-router-dom v6 的 createBrowserRouter
- / → HomePage（需登录保护）
- /login → LoginPage

**HomePage (src/pages/HomePage.tsx)：**
- 顶部导航栏：应用标题、用户头像/邮箱、登出按钮
- 主要内容区：最大宽度 3xl，居中
- AddTodoForm 组件
- FilterBar 组件
- TodoList 组件
- 响应式布局（移动端适配）

**认证守卫 (src/components/ProtectedRoute.tsx)：**
- 检查用户登录状态
- 未登录重定向到 /login，登录后重定向回原页面
- 加载中显示全屏加载动画

**main.tsx 入口文件：**
- 使用 AuthProvider 包裹 RouterProvider
- 配置 react-hot-toast 的 Toaster 组件

请生成完整可运行代码，确保所有页面和组件正确连接。`,
    },
    {
      id: 'sec-6',
      order: 6,
      title: 'Firestore 安全规则与部署',
      description: '配置 Firestore 安全规则，设置 Vercel 自动部署，准备项目上线。',
      estimatedHours: 2,
      difficulty: 'advanced',
      prompt: `请为一个 React + Firebase 项目完成部署配置。

**Firestore 安全规则 (firestore.rules)：**
- 只有认证用户可以读写
- 用户只能读写自己的待办事项（userId == request.auth.uid）
- 验证数据格式：title 不为空且长度 < 200，priority 必须是 low/medium/high 之一
- 禁止修改 userId 字段
- 添加 createdAt 自动设置规则

**Vercel 部署配置：**
- 创建 vercel.json，配置 SPA 路由重写（所有路由指向 index.html）
- 创建 .github/workflows/deploy.yml，实现：
  - 推送到 main 分支时自动部署
  - 包含 lint、type-check、build 步骤
  - 部署到 Vercel
- 环境变量配置说明（在 Vercel Dashboard 中设置 Firebase 配置）

**Firebase 索引配置：**
- 为 todo 集合创建复合索引：userId ASC + createdAt DESC
- 提供 firestore.indexes.json 文件内容

请确保配置完整，项目可以直接部署上线。`,
    },
  ],
  metadata: {
    totalSections: 6,
    estimatedTotalHours: 19,
    difficulty: 'intermediate',
    generatedAt: new Date().toISOString(),
  },
}
