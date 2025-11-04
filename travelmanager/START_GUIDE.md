# 启动指南

## 问题：找不到主类 com.vinn.travelmanager.TravelManagerApplication

### 解决方案

这个错误通常是因为：
1. Lombok 注解处理器在编译时没有正确生成 getter/setter 方法
2. 项目没有完全编译成功

### 方法1：使用 Spring Boot Maven 插件运行（推荐）

```bash
cd travelmanager
mvn spring-boot:run
```

这个命令会：
- 自动处理 Lombok 注解
- 编译并运行应用
- 无需手动编译

### 方法2：在 IDE 中运行

1. **IntelliJ IDEA**:
   - 确保安装了 Lombok 插件
   - File → Settings → Plugins → 搜索 "Lombok" → 安装
   - 确保启用注解处理器：File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors → 勾选 "Enable annotation processing"
   - 右键 `TravelManagerApplication.java` → Run

2. **Eclipse**:
   - 安装 Lombok：下载 lombok.jar，运行 `java -jar lombok.jar`，选择 Eclipse 安装目录
   - 重启 Eclipse
   - 右键项目 → Run As → Spring Boot App

3. **VS Code**:
   - 安装 "Language Support for Java" 扩展
   - 安装 "Lombok Annotations Support" 扩展
   - 右键 `TravelManagerApplication.java` → Run Java

### 方法3：重新编译并运行

```bash
cd travelmanager

# 清理并重新编译
mvn clean install -DskipTests

# 运行
mvn spring-boot:run
```

### 方法4：打包后运行

```bash
cd travelmanager

# 打包（跳过测试）
mvn clean package -DskipTests

# 运行 JAR 文件
java -jar target/travelmanager-1.0-SNAPSHOT.jar
```

## 验证 Lombok 是否工作

检查 `target/classes` 目录，应该能看到编译后的 class 文件。如果看到 `User.class`、`RegisterDTO.class` 等文件，说明编译成功。

## 常见问题

### 问题1：IDE 中显示红色错误，但代码能运行
- 这是 IDE 的提示问题，不影响运行
- 确保 IDE 安装了 Lombok 插件并启用了注解处理器

### 问题2：Maven 编译失败
- 检查 Java 版本是否为 17
- 检查 Maven 版本（建议 3.6+）
- 尝试删除 `target` 目录后重新编译

### 问题3：找不到依赖
```bash
# 强制更新依赖
mvn clean install -U
```

## 快速启动命令

最简单的方式：
```bash
cd travelmanager
mvn spring-boot:run
```

然后访问：http://localhost:8080

