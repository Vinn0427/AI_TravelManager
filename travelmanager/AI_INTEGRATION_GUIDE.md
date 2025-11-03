# 阿里百炼大模型集成指南

## 配置说明

### 1. 获取API Key
1. 访问 [阿里云百炼平台](https://dashscope.aliyun.com/)
2. 注册/登录账号
3. 创建API Key并复制

### 2. 配置API Key

有两种方式配置API Key：

#### 方式1：环境变量（推荐）
```bash
# Windows PowerShell
$env:DASHSCOPE_API_KEY="your-api-key-here"

# Linux/Mac
export DASHSCOPE_API_KEY="your-api-key-here"
```

#### 方式2：直接修改配置文件
编辑 `travelmanager/src/main/resources/application.yml`：
```yaml
ai:
  dashscope:
    api-key: your-api-key-here  # 直接填入你的API Key
```

### 3. 选择模型

在 `application.yml` 中可以修改模型：
```yaml
ai:
  dashscope:
    model: qwen-turbo  # 可选：qwen-turbo（快速）, qwen-plus（平衡）, qwen-max（高质量）
```

## API使用

### 前端调用
```javascript
import { aiApi } from '@/api/ai'

// 生成旅行计划
const response = await aiApi.generatePlan({
  destination: '北京',
  startDate: '2024-12-01',
  days: 5,
  budget: 10000,
  people: 2,
  preferences: ['美食', '文化'],
  requirements: '带孩子'
})
```

### 后端接口
- **URL**: `POST /api/ai/generate-plan`
- **请求体**:
```json
{
  "destination": "北京",
  "startDate": "2024-12-01",
  "days": 5,
  "budget": 10000,
  "people": 2,
  "preferences": ["美食", "文化"],
  "requirements": "带孩子"
}
```

- **响应**:
```json
{
  "code": 200,
  "message": "旅行计划生成成功",
  "data": {
    "destination": "北京",
    "startDate": "2024-12-01",
    "endDate": "2024-12-05",
    "totalDays": 5,
    "budget": 10000,
    "spots": [
      {
        "dayNumber": 1,
        "name": "天安门广场",
        "location": "北京市东城区",
        "latitude": 39.9042,
        "longitude": 116.4074
      }
    ],
    "budgets": [
      {
        "category": "交通",
        "amount": 2000
      },
      {
        "category": "住宿",
        "amount": 3000
      }
    ]
  }
}
```

## 功能说明

### 1. 旅行计划生成
- AI会根据用户输入的需求生成详细的旅行计划
- 包含景点推荐、预算分配等
- 返回的数据可直接保存到数据库

### 2. 数据解析
- AI返回的JSON数据会被自动解析为 `PlanSaveDTO`
- 包含Plan表所需的所有字段
- 包含Spot（景点）和Budget（预算）的详细信息

### 3. 保存功能
- 前端点击"保存行程到数据库"按钮
- 调用 `/api/plan/save` 接口
- 数据保存到plan、spot、budget三个表

## 注意事项

1. **API Key安全**：不要将API Key提交到代码仓库，使用环境变量或配置管理
2. **费用控制**：注意API调用次数和费用，建议设置使用限制
3. **网络要求**：需要能够访问 `dashscope.aliyuncs.com`
4. **错误处理**：如果AI服务不可用，会返回友好的错误提示

## 故障排查

### 问题1：API调用失败
- 检查API Key是否正确
- 检查网络连接
- 查看后端日志

### 问题2：JSON解析失败
- AI返回的数据格式可能不符合预期
- 查看后端日志中的原始响应
- 可以调整提示词以获得更好的JSON格式

### 问题3：保存失败
- 检查数据库连接
- 检查JWT token是否有效
- 查看后端日志错误信息

