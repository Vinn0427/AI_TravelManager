<template>
  <div class="home-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <h1 class="banner-title">欢迎回来，{{ userInfo?.username }}！</h1>
        <p class="banner-subtitle">让我们开始规划您的下一次精彩旅程吧</p>
        <el-button type="primary" size="large" @click="goToPlan">
          <el-icon><Plus /></el-icon>
          创建新行程
        </el-button>
      </div>
      <div class="banner-image">
        <el-icon :size="120" class="banner-icon"><Guide /></el-icon>
      </div>
    </div>

    <!-- 快捷功能卡片 -->
    <div class="quick-actions">
      <h2 class="section-title">快捷功能</h2>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="action-card" shadow="hover" @click="goToPlan">
            <div class="card-content">
              <el-icon :size="48" class="card-icon" color="#409eff"><MapLocation /></el-icon>
              <h3>智能规划</h3>
              <p>AI 为您定制专属行程</p>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="action-card" shadow="hover" @click="goToPlans">
            <div class="card-content">
              <el-icon :size="48" class="card-icon" color="#67c23a"><Document /></el-icon>
              <h3>我的行程</h3>
              <p>查看和管理您的行程</p>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="action-card" shadow="hover">
            <div class="card-content">
              <el-icon :size="48" class="card-icon" color="#e6a23c"><Calendar /></el-icon>
              <h3>费用管理</h3>
              <p>记录和追踪旅行支出</p>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="action-card" shadow="hover" @click="goToProfile">
            <div class="card-content">
              <el-icon :size="48" class="card-icon" color="#f56c6c"><User /></el-icon>
              <h3>个人中心</h3>
              <p>管理您的账户设置</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近行程 -->
    <div class="recent-plans">
      <div class="section-header">
        <h2 class="section-title">最近行程</h2>
        <el-button type="primary" text @click="goToPlans">查看全部</el-button>
      </div>
      <el-empty v-if="recentPlans.length === 0" description="暂无行程，快去创建您的第一个旅行计划吧！">
        <el-button type="primary" @click="goToPlan">创建行程</el-button>
      </el-empty>
      <el-row v-else :gutter="20">
        <el-col
          v-for="plan in recentPlans"
          :key="plan.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <el-card class="plan-card" shadow="hover">
            <div class="plan-header">
              <h3>{{ plan.title }}</h3>
              <el-tag :type="plan.status === '已完成' ? 'success' : 'primary'" size="small">
                {{ plan.status }}
              </el-tag>
            </div>
            <div class="plan-info">
              <p><el-icon><Location /></el-icon> {{ plan.destination }}</p>
              <p><el-icon><Calendar /></el-icon> {{ plan.date }}</p>
              <p><el-icon><User /></el-icon> {{ plan.days }} 天</p>
            </div>
            <div class="plan-footer">
              <el-button text type="primary" @click="viewPlan(plan.id)">查看详情</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Location,
  MapLocation,
  Document,
  Calendar,
  User,
  Plus,
  Guide
} from '@element-plus/icons-vue'
import { authUtils } from '@/utils/auth'

const router = useRouter()
const userInfo = ref(authUtils.getUserInfo())
const recentPlans = ref([])

// 获取最近行程（模拟数据）
onMounted(() => {
  // TODO: 从API获取真实数据
  recentPlans.value = []
})

const goToPlan = () => {
  router.push('/plan')
}

const goToPlans = () => {
  router.push('/plans')
}

const goToProfile = () => {
  router.push('/profile')
}

const viewPlan = (id) => {
  router.push(`/plans/${id}`)
}
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: white;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.banner-content {
  flex: 1;
}

.banner-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 12px;
}

.banner-subtitle {
  font-size: 16px;
  margin-bottom: 24px;
  opacity: 0.9;
}

.banner-image {
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-icon {
  opacity: 0.3;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}

.quick-actions {
  margin-bottom: 40px;
}

.action-card {
  cursor: pointer;
  transition: transform 0.3s;
  margin-bottom: 20px;
}

.action-card:hover {
  transform: translateY(-5px);
}

.card-content {
  text-align: center;
  padding: 10px;
}

.card-icon {
  margin-bottom: 16px;
}

.card-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.card-content p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.recent-plans {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.plan-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.plan-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.plan-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.plan-info {
  margin-bottom: 16px;
}

.plan-info p {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.plan-footer {
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .welcome-banner {
    flex-direction: column;
    text-align: center;
  }

  .banner-image {
    margin-top: 20px;
  }

  .banner-title {
    font-size: 24px;
  }
}
</style>

