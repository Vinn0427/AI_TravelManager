# AI 旅行规划师 (AI Travel Planner)

一个基于 Spring Boot + Vue3 的智能旅行规划 Web 应用，通过 AI 帮助用户规划个性化的旅行路线。

## 📋 项目简介

AI 旅行规划师旨在简化旅行规划过程，通过 AI 了解用户需求，自动生成详细的旅行路线和建议，并提供实时旅行辅助。

## ✨ 核心功能

1. **智能行程规划** - 用户可以通过语音或文字输入旅行需求，AI 自动生成个性化旅行路线
2. **费用预算与管理** - AI 进行预算分析，记录旅行开销
3. **用户管理与数据存储** - 注册登录系统，支持多份旅行计划管理
4. **云端行程同步** - 旅行计划、偏好设置、费用记录等数据云端同步

## 🛠️ 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis 3.0.3
- MySQL 8.0
- JWT 认证
- Lombok

### 前端
- Vue 3
- Element Plus
- Vue Router
- Axios
- Vite

## 📁 项目结构

```
travelmanager/
├── src/                          # 后端代码
│   └── main/
│       ├── java/                 # Java 源代码
│       │   └── com/vinn/travelmanager/
│       │       ├── controller/   # 控制器层
│       │       ├── service/      # 服务层
│       │       ├── mapper/       # 数据访问层
│       │       ├── entity/      # 实体类
│       │       └── ...
│       └── resources/            # 资源配置文件
│           ├── mapper/           # MyBatis Mapper XML
│           └── sql/             # SQL 建表语句
├── frontend/                     # 前端代码
│   ├── src/
│   │   ├── views/               # 页面组件
│   │   ├── components/         # 公共组件
│   │   ├── api/                # API 接口
│   │   └── utils/              # 工具类
│   └── package.json
└── pom.xml                      # Maven 配置
```

## 🚀 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+

### 1. 克隆项目

```bash
git clone https://github.com/Vinn0427/AI_TravelManager.git
cd AI_TravelManager
```

### 2. 数据库配置

#### 2.1 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS `travel_planner` 
DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2.2 执行建表语句

```bash
mysql -u root -p travel_planner < src/main/resources/sql/schema.sql
```

#### 2.3 修改数据库配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/travel_planner?...
    username: root
    password: 您的MySQL密码
```

详细配置说明请查看 [DATABASE_SETUP.md](./DATABASE_SETUP.md)

### 3. 启动后端服务

```bash
# 方式1: 使用 Maven
mvn spring-boot:run

# 方式2: 打包后运行
mvn clean package
java -jar target/travelmanager-1.0-SNAPSHOT.jar
```

后端服务将在 http://localhost:8080 启动

### 4. 启动前端服务

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 📚 API 文档

### 用户相关接口

#### 注册
```
POST /api/user/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "phone": "13800138000",
  "password": "Test123",
  "confirmPassword": "Test123",
  "agreement": true
}
```

#### 登录
```
POST /api/user/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "Test123",
  "remember": false
}
```

更多接口文档请参考项目代码或后端 README。

## 🔧 开发说明

### 后端开发

- 主启动类: `TravelManagerApplication.java`
- 配置文件: `src/main/resources/application.yml`
- MyBatis Mapper: `src/main/resources/mapper/`

### 前端开发

- 入口文件: `frontend/src/main.js`
- 路由配置: `frontend/src/router/index.js`
- API 接口: `frontend/src/api/`

## 📝 功能计划

- [x] 用户注册登录
- [x] 前端页面基础功能
- [ ] AI 行程生成（集成大语言模型）
- [ ] 语音识别功能
- [ ] 地图导航集成
- [ ] 费用管理功能
- [ ] 行程分享功能

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

## 👤 作者

Vinn0427

---

**注意**: 请确保在提交代码前移除敏感信息（如数据库密码），建议使用环境变量管理敏感配置。
