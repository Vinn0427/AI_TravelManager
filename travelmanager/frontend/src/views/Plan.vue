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
              </el-input>
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

      <!-- 右侧：语音输入和预览 -->
      <el-col :xs="24" :lg="14">
        <el-card class="preview-card">
          <template #header>
            <div class="card-header">
              <el-icon><Microphone /></el-icon>
              <span>语音输入（可选）</span>
            </div>
          </template>
          <div class="voice-section">
            <el-button
              type="primary"
              :icon="Microphone"
              circle
              :size="80"
              :loading="recording"
              @click="handleVoiceInput"
              class="voice-btn"
            />
            <p class="voice-hint">{{ recording ? '正在录音...' : '点击开始语音输入' }}</p>
          </div>
        </el-card>

        <el-card class="preview-card" style="margin-top: 20px;">
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
                  <el-divider />
                  <div class="result-details">
                    <div v-for="(day, index) in planResult.schedule" :key="index" class="day-schedule">
                      <h3>第 {{ index + 1 }} 天</h3>
                      <ul>
                        <li v-for="(item, i) in day" :key="i">{{ item }}</li>
                      </ul>
                    </div>
                  </div>
                  <el-button type="primary" style="width: 100%; margin-top: 20px;" @click="savePlan">
                    保存行程
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
  Guide
} from '@element-plus/icons-vue'

const planFormRef = ref(null)
const generating = ref(false)
const recording = ref(false)
const planResult = ref(null)

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

const handleGenerate = async () => {
  if (!planFormRef.value) return

  await planFormRef.value.validate((valid) => {
    if (valid) {
      generating.value = true
      
      // TODO: 调用后端API生成行程
      // 这里使用模拟数据
      setTimeout(() => {
        planResult.value = {
          title: `${planForm.destination} ${planForm.days}天${planForm.people}人游`,
          destination: planForm.destination,
          days: planForm.days,
          budget: planForm.budget,
          schedule: [
            ['上午：抵达目的地，入住酒店', '下午：游览城市中心，品尝当地美食', '晚上：自由活动'],
            ['上午：参观著名景点', '下午：体验当地文化', '晚上：观看演出'],
            ['上午：购物或自由活动', '下午：准备返程']
          ]
        }
        generating.value = false
        ElMessage.success('行程生成成功！')
      }, 2000)
    }
  })
}

const handleVoiceInput = () => {
  recording.value = !recording.value
  if (recording.value) {
    ElMessage.info('语音输入功能开发中...')
    // TODO: 集成语音识别API
  }
}

const savePlan = () => {
  // TODO: 保存行程到后端
  ElMessage.success('行程已保存！')
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

.voice-section {
  text-align: center;
  padding: 40px 0;
}

.voice-btn {
  margin-bottom: 16px;
}

.voice-hint {
  color: #909399;
  font-size: 14px;
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
</style>

