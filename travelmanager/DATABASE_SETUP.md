# 数据库配置和故障排除指南

## 错误现象

注册用户时出现 `HikariCP` 连接池错误，通常是数据库连接问题。

## 解决步骤

### 1. 确认 MySQL 服务已启动

#### Windows:
```bash
# 方式1: 打开服务管理器
# Win + R 输入 services.msc，找到 MySQL 服务，确保状态为"正在运行"

# 方式2: 使用命令行
net start MySQL
# 或者
net start MySQL80  # MySQL 8.0 的服务名可能是 MySQL80
```

#### macOS/Linux:
```bash
# 启动 MySQL 服务
sudo systemctl start mysql
# 或者
sudo service mysql start

# 检查状态
sudo systemctl status mysql
```

### 2. 检查数据库是否存在

使用 MySQL 客户端连接数据库：

```bash
mysql -u root -p
```

然后执行：

```sql
-- 查看所有数据库
SHOW DATABASES;

-- 如果 travel_planner 数据库不存在，创建它
CREATE DATABASE IF NOT EXISTS `travel_planner` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE travel_planner;

-- 查看表
SHOW TABLES;
```

### 3. 执行建表SQL

如果 `user` 表不存在，执行建表语句：

```bash
# 方式1: 在MySQL命令行中执行
mysql -u root -p travel_planner < src/main/resources/sql/schema.sql

# 方式2: 直接在MySQL客户端中复制粘贴执行
```

或者手动执行 `src/main/resources/sql/schema.sql` 文件中的 SQL 语句。

### 4. 检查数据库配置

编辑 `src/main/resources/application.yml`，确认以下配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/travel_planner?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root          # 修改为您的MySQL用户名
    password: root          # 修改为您的MySQL密码
```

**重要：**
- 如果 MySQL 不是默认端口 3306，请修改端口号
- 如果用户名不是 `root`，请修改 `username`
- **必须修改 `password` 为您的实际 MySQL 密码**

### 5. 测试数据库连接

使用 MySQL 客户端测试连接：

```bash
mysql -u root -p -h localhost -P 3306
```

如果无法连接，请检查：
- MySQL 服务是否运行
- 端口号是否正确
- 防火墙是否阻止连接

### 6. 常见问题

#### 问题1: 找不到数据库 `travel_planner`
**解决：** 执行步骤2创建数据库

#### 问题2: 找不到表 `user`
**解决：** 执行步骤3创建表

#### 问题3: 用户名或密码错误
**解决：** 
1. 确认 MySQL 用户名和密码
2. 修改 `application.yml` 中的配置
3. 重启 Spring Boot 应用

#### 问题4: 连接被拒绝
**解决：**
- 确认 MySQL 服务正在运行
- 检查端口号是否正确（默认3306）
- 检查防火墙设置

#### 问题5: 时区问题
如果出现时区错误，在连接URL中添加时区参数（已包含）：
```
serverTimezone=Asia/Shanghai
```

### 7. 验证配置

重启 Spring Boot 应用后，查看启动日志，应该看到：
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

如果看到连接错误，请根据错误信息检查上述步骤。

### 8. 快速检查清单

- [ ] MySQL 服务已启动
- [ ] 数据库 `travel_planner` 已创建
- [ ] 表 `user` 已创建
- [ ] `application.yml` 中的用户名和密码正确
- [ ] 端口号正确（默认3306）
- [ ] 防火墙允许连接

## 默认配置说明

项目默认配置：
- **数据库名：** `travel_planner`
- **用户名：** `root`
- **密码：** `root`（**请务必修改为您的实际密码**）
- **端口：** `3306`
- **主机：** `localhost`

## 修改配置后

修改 `application.yml` 后，需要：
1. 重新编译项目（如果使用 IDE）
2. 重启 Spring Boot 应用

```bash
# Maven 重新编译
mvn clean compile

# 重启应用
mvn spring-boot:run
```

