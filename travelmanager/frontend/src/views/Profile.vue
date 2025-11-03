<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧：个人信息卡片 -->
      <el-col :xs="24" :md="8">
        <el-card class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="100" class="user-avatar">
              <el-icon :size="50"><User /></el-icon>
            </el-avatar>
            <h2 class="username">{{ userInfo?.username || '用户' }}</h2>
            <p class="user-email">{{ userInfo?.email || '' }}</p>
          </div>
          <el-divider />
          <div class="user-stats">
            <div class="stat-item">
              <el-icon :size="24" color="#409eff"><Document /></el-icon>
              <div>
                <div class="stat-value">{{ userStats.plansCount || 0 }}</div>
                <div class="stat-label">我的行程</div>
              </div>
            </div>
            <div class="stat-item">
              <el-icon :size="24" color="#67c23a"><Calendar /></el-icon>
              <div>
                <div class="stat-value">{{ userStats.totalDays || 0 }}</div>
                <div class="stat-label">总旅行天数</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：账户设置 -->
      <el-col :xs="24" :md="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>账户设置</span>
            </div>
          </template>

          <el-tabs v-model="activeTab">
            <!-- 基本信息 -->
            <el-tab-pane label="基本信息" name="basic">
              <el-form
                ref="basicFormRef"
                :model="basicForm"
                :rules="basicRules"
                label-width="100px"
                style="max-width: 600px"
              >
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="basicForm.username" disabled />
                  <span class="form-hint">用户名不可修改</span>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="basicForm.email" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="basicForm.phone" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdateBasic">保存修改</el-button>
                  <el-button @click="handleResetBasic">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 修改密码 -->
            <el-tab-pane label="修改密码" name="password">
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="100px"
                style="max-width: 600px"
              >
                <el-form-item label="当前密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    show-password
                    placeholder="请输入当前密码"
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    show-password
                    placeholder="请输入新密码（至少6个字符）"
                  />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    show-password
                    placeholder="请再次输入新密码"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
                  <el-button @click="handleResetPassword">重置</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 偏好设置 -->
            <el-tab-pane label="偏好设置" name="preferences">
              <div class="preferences-section">
                <h3>旅行偏好</h3>
                <el-checkbox-group v-model="preferencesForm.preferences">
                  <el-checkbox label="美食">美食</el-checkbox>
                  <el-checkbox label="购物">购物</el-checkbox>
                  <el-checkbox label="文化">文化</el-checkbox>
                  <el-checkbox label="自然">自然</el-checkbox>
                  <el-checkbox label="历史">历史</el-checkbox>
                  <el-checkbox label="娱乐">娱乐</el-checkbox>
                </el-checkbox-group>
                <div style="margin-top: 20px;">
                  <el-button type="primary" @click="handleSavePreferences">保存偏好</el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Document, Calendar } from '@element-plus/icons-vue'
import { authUtils } from '@/utils/auth'
import { preferenceApi } from '@/api/preference'

const activeTab = ref('basic')
const basicFormRef = ref(null)
const passwordFormRef = ref(null)

const userInfo = ref(authUtils.getUserInfo())
const userStats = ref({
  plansCount: 0,
  totalDays: 0
})

const basicForm = reactive({
  username: userInfo.value?.username || '',
  email: userInfo.value?.email || '',
  phone: userInfo.value?.phone || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const preferencesForm = reactive({
  preferences: []
})

const basicRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$|^$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

onMounted(async () => {
  // TODO: 从API获取用户统计信息
  try {
    const res = await preferenceApi.get()
    if (res.code === 200 && res.data) {
      const labels = ['美食','购物','文化','自然','历史','娱乐']
      const text = [res.data.travelStyle].filter(Boolean).join('、')
      preferencesForm.preferences = labels.filter(l => text.includes(l))
    }
  } catch {}
})

const handleUpdateBasic = async () => {
  if (!basicFormRef.value) return

  await basicFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 调用更新API
      ElMessage.success('信息更新成功')
    }
  })
}

const handleResetBasic = () => {
  basicForm.username = userInfo.value?.username || ''
  basicForm.email = userInfo.value?.email || ''
  basicForm.phone = userInfo.value?.phone || ''
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 调用修改密码API
      ElMessage.success('密码修改成功，请重新登录')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    }
  })
}

const handleResetPassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

const handleSavePreferences = async () => {
  try {
    const travelStyle = (preferencesForm.preferences || []).join('、')
    const res = await preferenceApi.save({ travelStyle })
    if (res.code === 200) {
      ElMessage.success('偏好设置已保存')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card {
  text-align: center;
}

.avatar-section {
  padding: 20px 0;
}

.user-avatar {
  margin-bottom: 16px;
}

.username {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 12px 0 8px;
}

.user-email {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.user-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.form-hint {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

.preferences-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}
</style>

