# Pika AI Agent 前端项目

这是一个基于Vue3的AI聊天应用前端项目，包含AI恋爱大师和AI超级智能体两个应用。

## 项目结构

```
pika-ai-agent-frontend/
├── demo.html          # 演示文件，可直接在浏览器中打开
├── index.html         # Vue应用入口文件
├── package.json       # 项目依赖配置
├── vite.config.js     # Vite构建工具配置
├── src/               # 源代码目录
│   ├── main.js        # 应用入口文件
│   ├── App.vue        # 根组件
│   ├── router/        # 路由配置
│   │   └── index.js   # 路由定义
│   ├── views/         # 页面组件
│   │   ├── HomeView.vue      # 主页组件
│   │   ├── LoveAppView.vue    # AI恋爱大师应用
│   │   └── SuperAgentView.vue # AI超级智能体应用
│   ├── components/    # 公共组件
│   ├── assets/        # 静态资源
│   └── utils/         # 工具函数
└── README.md          # 项目说明文档
```

## 功能特点

1. **主页**：用于切换不同的AI应用
2. **AI恋爱大师应用**：聊天室风格界面，通过SSE调用后端`doChatWithLoveAppSseEmitter`接口
3. **AI超级智能体应用**：聊天室风格界面，通过SSE调用后端`doChatWithManus`接口

## 技术栈

- Vue 3
- Vue Router
- Pinia (状态管理)
- Axios (HTTP请求)
- Vite (构建工具)

## 快速开始

### 演示版本

由于npm权限问题，我们提供了一个演示版本，可以直接在浏览器中打开查看效果：

1. 打开 `demo.html` 文件
2. 在浏览器中查看应用效果

### 完整版本

要运行完整的Vue项目，需要先解决npm权限问题，然后执行以下步骤：

1. 安装依赖：
   ```bash
   npm install
   ```

2. 启动开发服务器：
   ```bash
   npm run dev
   ```

3. 构建生产版本：
   ```bash
   npm run build
   ```

## API接口

项目需要连接到后端API，接口地址前缀为：`http://localhost:8123/api`

### AI恋爱大师接口

- 接口路径：`/ai/love_app/chat/SseEmitter`
- 请求方法：GET
- 请求参数：
  - `message`: 用户消息
  - `chatId`: 聊天室ID

### AI超级智能体接口

- 接口路径：`/ai/agent/love_app`
- 请求方法：GET
- 请求参数：
  - `messages`: 用户消息

## 注意事项

1. 演示版本使用CDN引入Vue和相关库，实际项目中应使用npm安装依赖
2. 演示版本中的AI回复是模拟的，实际项目中需要连接后端API
3. SSE（Server-Sent Events）连接在演示版本中未实现，实际项目中需要使用Axios或EventSource实现

## 开发说明

1. 所有组件都使用Vue 3的Composition API编写
2. 使用Vue Router进行页面路由管理
3. 使用Pinia进行状态管理
4. 使用Axios进行HTTP请求
5. 使用SSE实现实时通信

## 许可证

MIT License