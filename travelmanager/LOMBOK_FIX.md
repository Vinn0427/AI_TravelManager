# Lombok 编译问题解决方案

## 问题描述
编译时出现 "找不到getter方法" 的错误，这是因为 Lombok 注解处理器没有正常工作。

## 解决方案

### 方案1: 使用 Maven 命令行编译和运行（推荐）

```bash
# 清理并编译项目
cd travelmanager
mvn clean compile

# 如果编译成功，运行项目
mvn spring-boot:run
```

### 方案2: 在 IDE 中配置注解处理器

#### IntelliJ IDEA
1. 安装 Lombok 插件
   - File → Settings → Plugins
   - 搜索 "Lombok" 并安装
   - 重启 IDEA

2. 启用注解处理器
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - 勾选 "Enable annotation processing"

3. 重新构建项目
   - Build → Rebuild Project

#### Eclipse
1. 安装 Lombok
   - 下载 lombok.jar
   - 双击运行，选择 Eclipse 安装目录
   - 重启 Eclipse

2. 启用注解处理器
   - Project → Properties → Java Build Path → Libraries
   - 添加 Lombok 到项目

### 方案3: 手动添加 getter/setter（临时方案）

如果 Lombok 仍然无法工作，可以手动为 DTO 和 Entity 类添加 getter/setter 方法。

## 验证

编译成功后，应该能在 `target/classes` 目录下看到编译后的 class 文件。

## 快速测试

```bash
# 编译项目
mvn clean compile

# 检查编译后的文件
ls target/classes/com/vinn/travelmanager/TravelManagerApplication.class

# 如果文件存在，运行项目
mvn spring-boot:run
```

