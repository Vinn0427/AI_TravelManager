# AI 旅行规划师 - 后端项目

## 技术栈

- Spring Boot 3.2.0
- MyBatis 3.0.3
- MySQL 8.0
- JWT (Json Web Token)
- Lombok
- FastJSON2

## 项目结构

```
src/main/java/com/vinn/travelmanager/
├── TravelManagerApplication.java    # 主启动类
├── common/                          # 通用类
│   ├── Result.java                  # 统一响应结果
│   └── ResultCode.java             # 响应状态码枚举
├── config/                          # 配置类
│   └── CorsConfig.java              # 跨域配置
├── controller/                      # 控制器层
│   └── UserController.java          # 用户控制器
├── dto/                             # 数据传输对象
│   ├── LoginDTO.java                # 登录DTO
│   └── RegisterDTO.java             # 注册DTO
├── entity/                          # 实体类
│   └── User.java                    # 用户实体
├── exception/                       # 异常处理
│   └── GlobalExceptionHandler.java  # 全局异常处理器
├── mapper/                          # Mapper接口
│   └── UserMapper.java              # 用户Mapper
├── service/                         # 服务接口
│   └── UserService.java             # 用户服务接口
├── service/impl/                    # 服务实现
│   └── UserServiceImpl.java         # 用户服务实现
└── util/                            # 工具类
    ├── JwtUtil.java                 # JWT工具类
    └── PasswordUtil.java            # 密码工具类
```

## 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE IF NOT EXISTS `travel_planner` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行建表语句：
   - 文件位置：`src/main/resources/sql/schema.sql`
   - 或者直接在MySQL中执行该SQL文件

3. 修改配置文件：
   - 编辑 `src/main/resources/application.yml`
   - 修改数据库连接信息（用户名、密码等）

## 运行项目

### 方式一：使用Maven运行
```bash
cd travelmanager
mvn spring-boot:run
```

### 方式二：打包后运行
```bash
cd travelmanager
mvn clean package
java -jar target/travelmanager-1.0-SNAPSHOT.jar
```

### 方式三：IDE运行
直接运行 `TravelManagerApplication.java` 的 main 方法

## API接口

### 1. 用户注册
- **URL**: `POST /api/user/register`
- **请求体**:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "phone": "13800138000",
  "password": "Test123",
  "confirmPassword": "Test123",
  "agreement": true
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "phone": "13800138000",
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T10:00:00"
    }
  },
  "timestamp": 1704067200000
}
```

### 2. 用户登录
- **URL**: `POST /api/user/login`
- **请求体**:
```json
{
  "username": "testuser",
  "password": "Test123",
  "remember": false
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "phone": "13800138000"
    }
  },
  "timestamp": 1704067200000
}
```

### 3. 获取用户信息
- **URL**: `GET /api/user/info`
- **请求头**: `Authorization: Bearer {token}`
- **响应**: 用户信息（待实现完整功能）

## 配置说明

### application.yml 配置项
- `server.port`: 服务端口（默认8080）
- `spring.datasource`: 数据库连接配置
- `mybatis`: MyBatis配置
- `jwt.secret`: JWT密钥
- `jwt.expiration`: JWT过期时间（毫秒）

## 密码加密

- 使用SHA-256算法加密
- 加盐处理：`password + SALT`
- 密码验证时使用相同的加密方式

## JWT Token

- Token包含用户ID和用户名
- 默认有效期24小时
- Token在响应头或请求头中传递：`Authorization: Bearer {token}`

## 错误处理

系统提供统一的错误响应格式：
```json
{
  "code": 500,
  "message": "错误信息",
  "data": null,
  "timestamp": 1704067200000
}
```

常见错误码：
- 200: 成功
- 400: 参数错误
- 401: 未授权
- 403: 禁止访问
- 404: 资源不存在
- 500: 系统错误
- 1001-1006: 用户相关错误

## 开发注意事项

1. 所有密码存储前必须加密
2. 返回用户信息时，必须清空密码字段
3. 使用逻辑删除，不物理删除数据
4. 所有时间字段使用数据库自动管理
5. 跨域配置已启用，支持前端调用

## 下一步开发

- [ ] 完善JWT认证拦截器
- [ ] 添加用户信息更新接口
- [ ] 实现密码修改功能
- [ ] 添加旅行计划相关功能
- [ ] 集成AI大语言模型API
- [ ] 添加地图API集成

