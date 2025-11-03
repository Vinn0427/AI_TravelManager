# 代码结构分析

本文件从“模块、数据结构、请求流、关键点与扩展性”四个方面描述本项目代码结构，便于维护与二次开发。

## 一、模块划分

- 后端（`travelmanager/`）
  - `controller/`
    - `PlanController`：计划的保存、详情、列表、删除。
    - `AIController`：调用 AI 生成旅行计划（后端进行提示词构造、请求路由与响应解析）。
    - `UserPreferenceController`：用户偏好保存与获取。
    - `BudgetController`：预算汇总与 AI 评价。
  - `service/`
    - `PlanService`：计划读写与关联数据的维护（spots/budgets/dailyguides）。
    - `AIService`：AI 能力接口（旅行计划生成、预算分析文本）。
    - `UserPreferenceService`：用户偏好的增改查。
  - `service/impl/`
    - `PlanServiceImpl`：保存/更新计划时的级联写入与清理；装配 `PlanDetailDTO`。
    - `AIServiceImpl`：RestTemplate 调用 DashScope，提示词构造与 JSON 解析；并提供 `analyzeBudget`。
    - `UserPreferenceServiceImpl`：按用户读写偏好。
  - `entity/`：
    - `Plan`、`Spot`、`Budget`、`DailyGuide`、`UserPreference`
  - `dto/`
    - `PlanSaveDTO`：保存计划入参，包含 `spots`、`budgets`、`dailyGuides`。
    - `PlanDetailDTO`：详情出参，包含 `spots`、`budgets`、`dailyGuides`。
    - `UserPreferenceDTO`：偏好保存的最小必要字段（本项目核心使用 `travelStyle`）。
    - `AIGenerateRequestDTO`：AI 生成接口入参。
  - `mapper/` + `resources/mapper/*.xml`
    - 每个实体对应 Mapper 接口与 XML（`PlanMapper`、`SpotMapper`、`BudgetMapper`、`DailyGuideMapper`、`UserPreferenceMapper`）。
  - `resources/sql/schema.sql`
    - 定义表结构与外键（`plan`、`spot`、`budget`、`user_preference`、`dailyguide`）。

- 前端（`travelmanager/frontend/`）
  - `src/views/`
    - `Plan.vue`：左侧表单（详情模式隐藏）、右侧行程预览；AI 生成与保存；详情模式从 `/plan/:id` 加载详情并只读展示。
    - `Plans.vue`：我的行程列表，点击跳转 `Plan.vue` 详情。
    - `Map.vue`：地图展示页，仅保留地点搜索 → 定位与唯一标记。
    - `BudgetAnalysis.vue`：预算汇总表与 AI 评价展示。
    - `Profile.vue`：偏好设置（勾选 → 保存为 `travelStyle` 文本）。
  - `src/api/`
    - `ai.js`、`plan.js`、`budget.js`、`preference.js`：对应后端 REST 接口。
  - `src/router/index.js`：路由表与登录校验；包含 `/plan/:id`、`/plans`、`/map`、`/budget`。
  - `src/components/Layout.vue`：侧边菜单（地图展示、预算分析）。
  - `src/utils/request.js`：Axios 实例（baseURL=`/api`，超时 60s，通用错误处理）。
  - `src/utils/speechRecognition.js`：Web Speech API 封装，用于目的地语音输入。

## 二、数据结构与表

- `plan`
  - 记录用户计划主信息：目的地、日期、总天数、预算等。
- `spot`
  - 每日景点信息，含经纬度与位置描述；`day_number` 与 `plan_id` 关联。
- `budget`
  - 预算项（交通/住宿/餐饮/景点/购物等）；用于预算分析与展示。
- `dailyguide`
  - 每日路线指引文本（`guide_text`），含 `day_number`。
- `user_preference`
  - 用户偏好，核心使用 `travel_style` 保存为中文文本（如“美食、文化”），在前端映射到复选框。

## 三、核心请求流

- AI 生成行程
  1. 前端 `Plan.vue` → `aiApi.generatePlan` 发送需求（目的地、日期、天数、预算、偏好等）。
  2. 后端 `AIServiceImpl` 构造提示词，调用 DashScope 并解析为 `PlanSaveDTO`（含 `spots`、`budgets`、`dailyGuides`）。
  3. 前端接收数据，通过 `formatPlanForDisplay` 标准化并展示。

- 保存计划
  1. 前端 `Plan.vue` 的“保存行程”整包提交 `PlanSaveDTO`（含 `dailyGuides`）。
  2. 后端 `PlanServiceImpl` 插入/更新 `plan`，并批量写入 `spot`、`budget`、`dailyguide`，更新时清理旧数据。

- 查看详情
  1. `Plans.vue` 跳转 `/plan/:id`。
  2. `Plan.vue` 根据路由参数加载 `PlanDetailDTO`，映射为展示模型，仅右侧预览（隐藏表单与保存按钮）。

- 地图搜索
  1. `Map.vue` 使用 AMap.Geocoder 获取地点坐标。
  2. 设置缩放、中心与唯一标记，并 setFitView 保证可见。

- 预算分析
  1. `BudgetAnalysis.vue` 调用 `/api/budget/analyze`。
  2. 后端聚合当前用户所有 `budget` 项（按类别），生成统计数据并调用 AI 返回中文评价。

- 用户偏好
  1. `Profile.vue` 勾选偏好 → `preferenceApi.save({ travelStyle: '美食、文化' })`。
  2. `Plan.vue` 非详情模式下加载偏好 `travelStyle` 并映射到复选框。

## 四、关键设计与可扩展点

- AI 调用
  - 已将 `result_format` 与 `messages` 适配 DashScope 要求；可切换模型与调参（`temperature`、`top_p`）。
  - 解析时对 `dailyGuides` 做健壮性校验并记录日志，避免前端展示空白。
- 性能与超时
  - 服务端与客户端均设置 60s 超时；切换更快模型以保障多天行程。
- 数据一致性
  - 保存计划时采用事务，更新先清理再批量写入关联数据，保证数据与前端所见一致。
- 安全
  - 通过 JWT 获取 `userId`，所有用户数据读写均基于 token 进行授权校验。
- 偏好模型
  - 当前简化为 `travelStyle` 文本；可平滑扩展为结构化多字段（如 `preferred_transport` 等），前端映射即可。
- 地图
  - 已预留高德地图二次开发空间（路线规划、附近检索等）。

## 五、开发与运维建议

- 日志：AI 交互与解析处日志较多，生产环境注意调整等级。
- 错误处理：前后端统一友好提示信息，便于用户理解与重试。
- 配置管理：将敏感 Key 放入环境变量与私密配置；前端仅注入必要 Key。
- 测试：建议补充单元测试（DTO 解析、Service 事务）、E2E（生成→保存→查看→删除）。


