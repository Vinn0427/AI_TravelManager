# TravelManager 项目说明

一个基于前后端分离的智能旅行规划应用，支持 AI 行程生成、行程保存与展示、地图地点搜索展示、预算分析与 AI 评价、用户旅行偏好设置与默认填充、目的地语音输入等核心能力。

## 功能特性

- 智能旅行规划（AI）
  - 集成阿里百炼 DashScope（模型：qwen-turbo），根据目的地、时间、预算、人数与偏好生成行程。
  - 规划结果包含：预算分配、每日景点与“每日路线指引”（交通/住宿/景点/餐饮）。
  - 前端右侧实时展示计划结果并支持保存。
- 行程管理
  - 保存/更新/删除计划；新库表模型：`plan`、`spot`、`budget`、`dailyguide`，按计划与天数管理细分数据。
  - “我的行程”列表可查看详情，跳转回规划页只读展示预览（隐藏左侧表单与保存按钮）。
- 地图展示
  - 高德地图前端集成，通过环境变量加载 API Key。
  - 地图页支持地点搜索并自动定位与标记，仅保留一个搜索标记。
- 预算分析
  - 汇总当前用户所有行程的预算（按类别），展示统计表与总额、计划数，并调用 AI 返回中文评价建议。
- 用户偏好
  - 用户在“个人中心”设置旅行偏好；保存到 `user_preference.travel_style`。
  - 进入规划页（非详情模式）默认加载用户偏好并勾选。
- 语音输入
  - Web Speech API（前端）实现目的地语音识别按钮，仅用于目的地字段。
- 健壮性与性能
  - 统一错误提示；前后端超时均调整为 60s；切换更快的模型以降低超时概率。

## 技术栈

- 前端：Vue 3、Element Plus、Vue Router、Axios、Vite
- 后端：Spring Boot、MyBatis、MySQL、Lombok、JWT、RestTemplate
- AI：阿里百炼 DashScope（文本生成服务）
- 地图：高德地图 JavaScript API（通过环境变量注入 API Key）

## 目录结构

- `travelmanager/` 后端工程
  - `src/main/java/com/vinn/travelmanager/`
    - `controller/`：`PlanController`、`UserPreferenceController`、`AIController`、`BudgetController`
    - `service/`：接口层（`PlanService`、`AIService`、`UserPreferenceService`）
    - `service/impl/`：实现层（含 `AIServiceImpl` DashScope 集成）
    - `entity/`：`Plan`、`Spot`、`Budget`、`UserPreference`、`DailyGuide`
    - `dto/`：`PlanSaveDTO`、`PlanDetailDTO`、`UserPreferenceDTO`、`AIGenerateRequestDTO`
    - `mapper/`：MyBatis Mapper 接口
  - `src/main/resources/`
    - `mapper/*.xml`：MyBatis XML
    - `sql/schema.sql`：数据库建表 SQL（含 `dailyguide`）
    - `application.yml`：后端配置（DashScope 配置与模型、数据库等）
- `travelmanager/frontend/` 前端工程
  - `src/views/`：`Plan.vue`、`Plans.vue`、`Map.vue`、`BudgetAnalysis.vue`、`Profile.vue`
  - `src/components/`：`Layout.vue`
  - `src/router/`：`index.js`
  - `src/api/`：后端接口封装（`ai.js`、`plan.js`、`budget.js`、`preference.js`）
  - `src/utils/`：`request.js`（Axios 实例）、`speechRecognition.js`（语音识别工具）
  - `vite.config.js`、`index.html`、`.gitignore`

更详细的代码结构请参见 `CODE_STRUCTURE.md`。

## 环境变量

- 前端（Vite）：
  - `VITE_MAP_API_KEY` 或系统环境变量 `MAP_API_KEY`（通过动态脚本注入）。
- 后端（`application.yml`）：
  - `ai.dashscope.api-key`: DashScope API Key
  - `ai.dashscope.base-url`: `https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation`
  - `ai.dashscope.model`: `qwen-turbo`

## 数据库

执行 `travelmanager/src/main/resources/sql/schema.sql` 创建表：
- `plan`、`spot`、`budget`、`user_preference`、`dailyguide`
- 外键与级联删除：删除 plan 时自动清理关联数据。

## 运行与构建

1) 后端
- 配置数据库与 DashScope Key 后，进入 `travelmanager/` 目录：
```
mvn clean package -DskipTests
mvn spring-boot:run
```
默认端口：`http://localhost:8080`

2) 前端
- 进入 `travelmanager/frontend/`：
```
npm install
npm run dev
```
默认端口：`http://localhost:5173`

确保浏览器可访问高德地图并已配置 `VITE_MAP_API_KEY`。

## 关键 API 概览（后端）

- 计划 Plan
  - `POST /api/plan/save` 保存/更新计划（包含 spots、budgets、dailyGuides）
  - `GET /api/plan/{id}` 获取计划详情
  - `GET /api/plan/list` 获取我的所有计划
  - `DELETE /api/plan/{id}` 删除计划
- AI
  - `POST /api/ai/generate` 生成旅行计划（后端转发 DashScope）
- 偏好
  - `GET /api/preference` 获取我的偏好
  - `POST /api/preference/save` 保存偏好（最小字段：`travelStyle`）
- 预算
  - `GET /api/budget/analyze` 汇总当前用户预算并调用 AI 返回评价

所有接口需携带 `Authorization: Bearer <token>`（登录流程参考现有项目）。

## 前端界面说明

- 旅行规划 `Plan.vue`
  - 左侧：表单（详情模式隐藏）与目的地语音输入按钮；右侧：地图占位和 AI 生成结果预览。
  - 详情模式：从 `/plan/:id` 进入，仅展示右侧预览（隐藏“保存行程”）。
- 我的行程 `Plans.vue`：列出计划，点击“查看详情”跳转到 `Plan.vue` 详情模式。
- 地图展示 `Map.vue`：搜索地点并在地图上定位与标记（保留唯一标记）。
- 预算分析 `BudgetAnalysis.vue`：类别汇总表 + 总额/计划数 + AI 文字评价。
- 个人中心 `Profile.vue`：偏好设置（“美食/购物/文化/自然/历史/娱乐”），保存后用于规划页默认勾选。

## 超时与性能

- 后端 `RestTemplate`：连接 10s，读取 60s。
- 前端 Axios：超时 60s，针对 AI 多天行程提供友好提示。
- 模型选择：`qwen-turbo` 相对更快，减少超时概率。

## 常见问题

- Git 提交乱码：确保执行
```
git config --global i18n.commitencoding utf-8
git config --global i18n.logoutputencoding utf-8
```
- DashScope 400 Bad Request：确认请求体包含 `input.messages` 与 `parameters`。
- dailyGuides 不显示：确认 AI 返回 `dailyGuides` 数组且前端 `sortedDailyGuides` 为非空。
- 地图不显示：确认加载了 API Key 且网络可访问高德地图资源。

## 许可

此项目仅用于学习与演示用途。


