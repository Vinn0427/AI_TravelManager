# AI 旅行规划师 - 前端项目

## 技术栈

- Vue 3
- Element Plus
- Vue Router
- Vite
- Axios

## 项目结构

```
frontend/
├── src/
│   ├── views/          # 页面组件
│   │   ├── Login.vue   # 登录页面
│   │   └── Register.vue # 注册页面
│   ├── router/         # 路由配置
│   ├── style/          # 样式文件
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── index.html
├── vite.config.js      # Vite 配置
└── package.json
```

## 安装依赖

```bash
cd travelmanager/frontend
npm install
```

## 开发运行

```bash
npm run dev
```

访问 http://localhost:3000

## 构建生产版本

```bash
npm run build
```

## 功能说明

### 登录页面 (`/login`)
- 用户名/邮箱登录
- 密码输入（支持显示/隐藏）
- 记住我功能
- 忘记密码链接
- 表单验证

### 注册页面 (`/register`)
- 用户名注册
- 邮箱注册
- 手机号（可选）
- 密码确认
- 用户协议确认
- 完整的表单验证

## 样式特点

- 现代化渐变背景
- 毛玻璃效果卡片
- 流畅的动画过渡
- 响应式设计
- 美观的图标展示

## 下一步

- [ ] 连接后端API
- [ ] 实现用户认证
- [ ] 添加主页和行程规划页面
- [ ] 集成语音识别功能

