# 高德地图API Key配置说明

## 配置方式

### 方式1：使用环境变量文件（推荐）

1. 在 `frontend` 目录下创建 `.env` 文件：
```bash
cd travelmanager/frontend
touch .env  # Linux/Mac
# 或使用文本编辑器创建 .env 文件（Windows）
```

2. 在 `.env` 文件中添加：
```env
VITE_MAP_API_KEY=your-map-api-key-here
```

3. 重启开发服务器

### 方式2：使用系统环境变量

#### Windows PowerShell
```powershell
$env:MAP_API_KEY="your-map-api-key-here"
# 然后启动项目
npm run dev
```

#### Windows CMD
```cmd
set MAP_API_KEY=your-map-api-key-here
npm run dev
```

#### Linux/Mac
```bash
export MAP_API_KEY=your-map-api-key-here
npm run dev
```

**注意**：Vite要求环境变量必须以 `VITE_` 开头才能在前端代码中访问。如果使用系统环境变量 `MAP_API_KEY`，需要在代码中通过 `process.env.MAP_API_KEY` 访问（仅在Node.js环境中可用）。

**推荐使用方式1**，创建 `.env` 文件并配置 `VITE_MAP_API_KEY`。

### 方式3：直接在代码中配置（不推荐，仅用于测试）

如果需要在代码中直接配置，可以修改 `src/main.js` 中的这一行：
```javascript
const apiKey = import.meta.env.VITE_MAP_API_KEY || 'your-api-key-here'
```

## 获取高德地图API Key

1. 访问 [高德开放平台](https://lbs.amap.com/)
2. 注册/登录账号
3. 进入控制台 → 应用管理 → 我的应用 → 创建新应用
4. 添加 Key，选择平台类型为"Web端(JS API)"
5. 复制生成的 Key

## 验证配置

1. 启动前端开发服务器：`npm run dev`
2. 打开浏览器控制台
3. 如果看到"高德地图API加载成功"，说明配置正确
4. 如果看到"高德地图API加载失败"，请检查：
   - API Key是否正确
   - `.env` 文件是否在正确的位置
   - 是否重启了开发服务器

## 注意事项

1. **不要将 `.env` 文件提交到Git仓库**（已添加到 `.gitignore`）
2. **API Key要保密**，不要分享给他人
3. **需要设置安全域名**：在高德开放平台中配置允许使用该Key的域名
4. **环境变量修改后需要重启开发服务器**

