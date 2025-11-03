# 修复阿里百炼API调用格式

## 问题描述
调用阿里百炼API时出现错误：
```
400 Bad Request: "Either \"prompt\" or \"messages\" must exist and cannot both be none"
```

## 原因分析
阿里百炼API的请求格式要求：
- `messages` 必须放在 `input` 对象中
- 其他参数（如 `temperature`, `top_p`, `result_format`）必须放在 `parameters` 对象中

## 修复后的正确格式

### 请求体结构
```json
{
    "model": "qwen-plus",
    "input": {
        "messages": [
            {
                "role": "user",
                "content": "你的提示词内容"
            }
        ]
    },
    "parameters": {
        "temperature": 0.7,
        "top_p": 0.9,
        "result_format": "message"
    }
}
```

### 响应格式
```json
{
    "output": {
        "choices": [
            {
                "message": {
                    "content": "AI返回的内容"
                }
            }
        ]
    }
}
```

## 已修复的代码
- `AIServiceImpl.java` 中的 `callDashScopeAPI` 方法
- 请求体构建逻辑已更新为正确的格式
- 响应解析逻辑已优化，增加了容错处理

## 验证方法
1. 确保API Key已正确配置
2. 测试生成旅行计划功能
3. 检查后端日志确认API调用成功

