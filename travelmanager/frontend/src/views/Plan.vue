<template>
  <div class="plan-container">
    <div class="plan-header">
      <h1 class="page-title">智能旅行规划</h1>
      <p class="page-subtitle">告诉我们您的旅行需求，AI 将为您生成完美的旅行路线</p>
    </div>

    <el-row :gutter="20">
      <!-- 左侧：输入表单 -->
      <el-col :xs="24" :lg="10">
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <el-icon><Edit /></el-icon>
              <span>填写旅行需求</span>
            </div>
          </template>

          <el-form
            ref="planFormRef"
            :model="planForm"
            :rules="planRules"
            label-position="top"
          >
            <el-form-item label="目的地" prop="destination">
              <el-input
                v-model="planForm.destination"
                placeholder="例如：日本、北京、巴黎"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><Location /></el-icon>
                </template>
                <template #suffix>
                  <el-button
                    :icon="Microphone"
                    circle
                    :loading="recording"
                    @click="handleVoiceInput"
                    :type="recording ? 'danger' : 'primary'"
                    text
                    title="语音输入目的地"
                  />
                </template>
              </el-input>
              <div v-if="recording" class="recording-hint">
                <el-icon class="recording-icon"><Microphone /></el-icon>
                <span>正在录音，请说出目的地...</span>
              </div>
            </el-form-item>

            <el-form-item label="出发日期" prop="startDate">
              <el-date-picker
                v-model="planForm.startDate"
                type="date"
                placeholder="选择出发日期"
                size="large"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>

            <el-form-item label="旅行天数" prop="days">
              <el-input-number
                v-model="planForm.days"
                :min="1"
                :max="30"
                size="large"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="预算（元）" prop="budget">
              <el-input-number
                v-model="planForm.budget"
                :min="0"
                :step="100"
                size="large"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="同行人数" prop="people">
              <el-input-number
                v-model="planForm.people"
                :min="1"
                :max="20"
                size="large"
                style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="旅行偏好">
              <el-checkbox-group v-model="planForm.preferences">
                <el-checkbox label="美食">美食</el-checkbox>
                <el-checkbox label="购物">购物</el-checkbox>
                <el-checkbox label="文化">文化</el-checkbox>
                <el-checkbox label="自然">自然</el-checkbox>
                <el-checkbox label="历史">历史</el-checkbox>
                <el-checkbox label="娱乐">娱乐</el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="特殊需求">
              <el-input
                v-model="planForm.requirements"
                type="textarea"
                :rows="4"
                placeholder="例如：带孩子、老人同行、蜜月旅行等"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="generating"
                @click="handleGenerate"
                style="width: 100%"
              >
                <el-icon><MagicStick /></el-icon>
                {{ generating ? '正在生成行程...' : '生成智能行程' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：行程预览 -->
      <el-col :xs="24" :lg="14">
        <el-card class="preview-card">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>行程预览</span>
            </div>
          </template>
          <div v-if="!planResult" class="empty-preview">
            <el-empty description="填写左侧表单并点击生成，AI 将为您创建专属行程">
              <el-icon :size="60" class="empty-icon"><Guide /></el-icon>
            </el-empty>
          </div>
          <div v-else class="plan-result">
            <el-skeleton :loading="generating" animated>
              <template #template>
                <el-skeleton-item variant="h1" style="width: 40%" />
                <br />
                <el-skeleton-item variant="text" style="width: 60%" />
                <el-skeleton-item variant="text" style="width: 80%" />
              </template>
              <template #default>
                <div class="result-content">
                  <h2>{{ planResult.title }}</h2>
                  <div class="result-info">
                    <el-tag>{{ planResult.destination }}</el-tag>
                    <el-tag type="success">{{ planResult.days }} 天</el-tag>
                    <el-tag type="warning">预算: ¥{{ planResult.budget }}</el-tag>
                  </div>
                  
                  <!-- 预算分配 -->
                  <div v-if="planResult.budgets && planResult.budgets.length > 0" class="budget-section">
                    <el-divider />
                    <h3 class="section-title">预算分配</h3>
                    <div class="budget-list">
                      <div v-for="(budget, index) in planResult.budgets" :key="index" class="budget-item">
                        <span class="budget-category">{{ budget.category }}</span>
                        <span class="budget-amount">¥{{ budget.amount }}</span>
                      </div>
                    </div>
                  </div>

                  <el-divider />
                  
                  <!-- 每日行程 -->
                  <div class="result-details">
                    <h3 class="section-title">每日行程</h3>
                    <div v-for="(day, index) in planResult.schedule" :key="index" class="day-schedule">
                      <h3 class="day-title">
                        <el-icon><Calendar /></el-icon>
                        第 {{ index + 1 }} 天
                      </h3>
                      <ul class="day-list">
                        <li v-for="(item, i) in day" :key="i" class="day-item">
                          <el-icon class="item-icon"><Location /></el-icon>
                          {{ item }}
                        </li>
                      </ul>
                    </div>
                  </div>

                  <el-divider />
                  
                  <el-button type="primary" style="width: 100%; margin-top: 20px;" @click="savePlan">
                    保存行程到数据库
                  </el-button>
                </div>
              </template>
            </el-skeleton>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Location,
  Edit,
  MagicStick,
  Microphone,
  Document,
  Guide,
  Calendar,
  Van,
  House,
  Timer
} from '@element-plus/icons-vue'
import { aiApi } from '@/api/ai'
import { planApi } from '@/api/plan'

const planFormRef = ref(null)
const generating = ref(false)
const recording = ref(false)
const planResult = ref(null)
const planData = ref(null) // 保存从AI获取的完整数据用于保存

const planForm = reactive({
  destination: '',
  startDate: '',
  days: 3,
  budget: 5000,
  people: 1,
  preferences: [],
  requirements: ''
})

const planRules = {
  destination: [
    { required: true, message: '请输入目的地', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择出发日期', trigger: 'change' }
  ],
  days: [
    { required: true, message: '请输入旅行天数', trigger: 'blur' }
  ],
  budget: [
    { required: true, message: '请输入预算', trigger: 'blur' }
  ],
  people: [
    { required: true, message: '请输入同行人数', trigger: 'blur' }
  ]
}

// 生成行程
const handleGenerate = async () => {
  if (!planFormRef.value) return

  await planFormRef.value.validate(async (valid) => {
    if (valid) {
      generating.value = true
      
      try {
        // 调用后端AI接口生成行程
        const response = await aiApi.generatePlan({
          destination: planForm.destination,
          startDate: planForm.startDate,
          days: planForm.days,
          budget: planForm.budget,
          people: planForm.people,
          preferences: planForm.preferences,
          requirements: planForm.requirements
        })

        if (response.code === 200 && response.data) {
          // 保存完整的计划数据（用于后续保存到数据库）
          planData.value = response.data
          
          // 格式化数据用于前端展示
          planResult.value = formatPlanForDisplay(response.data)
          
          ElMessage.success('AI行程生成成功！')
        } else {
          ElMessage.error(response.message || '生成行程失败')
        }
      } catch (error) {
        console.error('生成行程错误:', error)
        ElMessage.error(error.message || '生成行程失败，请检查网络连接或AI服务配置')
      } finally {
        generating.value = false
      }
    }
  })
}

// 格式化计划数据用于前端展示
const formatPlanForDisplay = (planData) => {
  // 按天数组织景点
  const scheduleByDay = {}
  
  if (planData.spots && planData.spots.length > 0) {
    planData.spots.forEach(spot => {
      const day = spot.dayNumber || 1
      if (!scheduleByDay[day]) {
        scheduleByDay[day] = []
      }
      scheduleByDay[day].push(spot.name + (spot.location ? ` (${spot.location})` : ''))
    })
  }

  // 转换为数组格式用于展示
  const schedule = []
  for (let i = 1; i <= planData.totalDays; i++) {
    schedule.push(scheduleByDay[i] || [`第${i}天的行程安排`])
  }

  return {
    title: `${planData.destination} ${planData.totalDays}天${planForm.people}人游`,
    destination: planData.destination,
    days: planData.totalDays,
    budget: planData.budget,
    schedule: schedule,
    spots: planData.spots || [],
    budgets: planData.budgets || []
  }
}

// 语音输入处理
const handleVoiceInput = () => {
  if (recording.value) {
    // 停止录音
    recording.value = false
    ElMessage.info('录音已停止')
    // TODO: 处理语音识别结果，填充到目的地
  } else {
    // 开始录音
    recording.value = true
    ElMessage.info('开始录音，请说出目的地...')
    // TODO: 集成语音识别API（如科大讯飞等）
    // 模拟语音识别结果（3秒后自动填充）
    setTimeout(() => {
      if (recording.value) {
        // 模拟识别结果
        planForm.destination = '北京'
        recording.value = false
        ElMessage.success('已识别目的地：北京')
      }
    }, 3000)
  }
}

// 保存行程到数据库
const savePlan = async () => {
  if (!planData.value) {
    ElMessage.warning('请先生成行程')
    return
  }

  try {
    // 构建保存数据（使用从AI获取的完整数据）
    const saveData = {
      destination: planData.value.destination,
      startDate: planData.value.startDate,
      endDate: planData.value.endDate,
      totalDays: planData.value.totalDays,
      budget: planData.value.budget,
      spots: planData.value.spots || [],
      budgets: planData.value.budgets || []
    }

    const response = await planApi.savePlan(saveData)
    if (response.code === 200) {
      ElMessage.success('行程已保存到数据库！')
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存行程错误:', error)
    ElMessage.error(error.message || '保存失败，请检查网络连接')
  }
}
</script>

<style scoped>
.plan-container {
  max-width: 1400px;
  margin: 0 auto;
}

.plan-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
}

.form-card,
.preview-card {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.recording-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 8px;
  color: #f56c6c;
  font-size: 12px;
  animation: pulse 1.5s ease-in-out infinite;
}

.recording-icon {
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.empty-preview {
  padding: 60px 0;
  text-align: center;
}

.empty-icon {
  color: #c0c4cc;
  margin-top: 20px;
}

.plan-result {
  padding: 20px 0;
}

.result-content h2 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.result-info {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.result-details {
  margin-top: 20px;
}

.day-schedule {
  margin-bottom: 24px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.day-schedule h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.day-schedule ul {
  margin: 0;
  padding-left: 20px;
}

.day-schedule li {
  margin-bottom: 8px;
  color: #606266;
  line-height: 1.6;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  margin-top: 8px;
}

.budget-section {
  margin-top: 20px;
}

.budget-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.budget-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 6px;
}

.budget-category {
  font-weight: 500;
  color: #303133;
}

.budget-amount {
  font-weight: 600;
  color: #409eff;
  font-size: 16px;
}

.day-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.day-list {
  margin: 0;
  padding-left: 20px;
  list-style: none;
}

.day-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 10px;
  color: #606266;
  line-height: 1.6;
  padding: 6px 0;
}

.item-icon {
  color: #409eff;
  margin-top: 4px;
  flex-shrink: 0;
}
</style>

