# 修复 ClassNotFoundException: TravelPlanAttraction

## 问题原因
错误 `Cannot find class: com.vinn.travelmanager.entity.TravelPlanAttraction` 通常是由以下原因引起的：

1. **编译缓存**：Maven的 `target` 目录中可能还有旧的编译类文件
2. **IDE缓存**：IDE（如IntelliJ IDEA）可能缓存了旧的类引用
3. **MyBatis扫描**：MyBatis在启动时会扫描实体类，如果缓存中有旧类的引用会导致错误

## 解决方案

### 方法1：清理Maven编译缓存（推荐）

```bash
# Windows PowerShell
cd travelmanager
mvn clean
mvn compile
```

或者在项目根目录执行：
```bash
cd F:\cursorCode\travelmanager
mvn clean install -DskipTests
```

### 方法2：手动删除target目录

```powershell
# Windows PowerShell
Remove-Item -Recurse -Force travelmanager\target
```

### 方法3：清理IDE缓存

**IntelliJ IDEA:**
1. 菜单：File → Invalidate Caches / Restart
2. 选择 "Invalidate and Restart"

**Eclipse:**
1. 右键项目 → Clean
2. Project → Clean → Clean all projects

### 方法4：验证代码一致性

已确认以下内容都是正确的：

✅ **实体类**：
- `Plan.java` (已创建)
- `Spot.java` (已创建)
- `Budget.java` (已创建)
- `UserPreference.java` (已创建)

✅ **Mapper XML**：
- `PlanMapper.xml` (使用 `com.vinn.travelmanager.entity.Plan`)
- `SpotMapper.xml` (使用 `com.vinn.travelmanager.entity.Spot`)
- `BudgetMapper.xml` (使用 `com.vinn.travelmanager.entity.Budget`)
- `UserPreferenceMapper.xml` (使用 `com.vinn.travelmanager.entity.UserPreference`)

✅ **Service和Controller**：
- 所有代码都使用新的实体类（Plan, Spot, Budget, UserPreference）
- 没有对旧类（TravelPlan, TravelPlanDay, TravelPlanAttraction, TravelPlanRestaurant）的引用

## 验证步骤

1. 确认实体类目录：
   ```
   travelmanager/src/main/java/com/vinn/travelmanager/entity/
   ├── Budget.java
   ├── Plan.java
   ├── Spot.java
   ├── User.java
   └── UserPreference.java
   ```

2. 确认没有旧的实体类：
   ```bash
   # 不应该存在这些文件
   - TravelPlan.java
   - TravelPlanDay.java
   - TravelPlanAttraction.java
   - TravelPlanRestaurant.java
   ```

3. 重新编译并运行：
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## 如果问题仍然存在

如果清理后问题仍然存在，请检查：

1. **检查是否有其他模块或依赖引用了旧的类**
2. **检查IDE的索引是否正确更新**
3. **尝试重启IDE和重新导入Maven项目**

