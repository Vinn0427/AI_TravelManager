<template>
  <div class="plans-container">
    <div class="page-header">
      <h1 class="page-title">我的行程</h1>
      <el-button type="primary" @click="goToPlan">
        <el-icon><Plus /></el-icon>
        创建新行程
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <el-card class="filter-card" style="margin-bottom: 20px;">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部" clearable style="width: 150px">
            <el-option label="全部" value="" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item label="目的地">
          <el-input v-model="filterForm.destination" placeholder="搜索目的地" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">筛选</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 行程列表 -->
    <el-empty v-if="plans.length === 0" description="暂无行程，快去创建您的第一个旅行计划吧！">
      <el-button type="primary" @click="goToPlan">创建行程</el-button>
    </el-empty>

    <el-row v-else :gutter="20">
      <el-col
        v-for="plan in plans"
        :key="plan.id"
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
      >
        <el-card class="plan-card" shadow="hover">
          <div class="plan-image">
            <el-icon :size="60" class="plan-icon"><MapLocation /></el-icon>
          </div>
          <div class="plan-content">
            <div class="plan-header">
              <h3>{{ plan.title }}</h3>
              <el-tag :type="getStatusType(plan.status)" size="small">
                {{ plan.status }}
              </el-tag>
            </div>
            <div class="plan-info">
              <p><el-icon><Location /></el-icon> {{ plan.destination }}</p>
              <p><el-icon><Calendar /></el-icon> {{ plan.startDate }} - {{ plan.endDate }}</p>
              <p><el-icon><Timer /></el-icon> {{ plan.days }} 天</p>
              <p><el-icon><Money /></el-icon> 预算: ¥{{ plan.budget }}</p>
            </div>
            <div class="plan-footer">
              <el-button text type="primary" @click="viewPlan(plan.id)">查看详情</el-button>
              <el-button text type="danger" @click="deletePlan(plan.id)">删除</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Location,
  Calendar,
  Timer,
  Money,
  MapLocation,
  Plus
} from '@element-plus/icons-vue'

const router = useRouter()
const filterForm = reactive({
  status: '',
  destination: ''
})

const plans = ref([])

// 获取行程列表（模拟数据）
onMounted(() => {
  // TODO: 从API获取真实数据
  plans.value = []
})

const goToPlan = () => {
  router.push('/plan')
}

const handleFilter = () => {
  // TODO: 实现筛选逻辑
  ElMessage.info('筛选功能开发中...')
}

const handleReset = () => {
  filterForm.status = ''
  filterForm.destination = ''
  handleFilter()
}

const viewPlan = (id) => {
  router.push(`/plans/${id}`)
}

const deletePlan = (id) => {
  ElMessageBox.confirm('确定要删除这个行程吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // TODO: 调用删除API
    ElMessage.success('删除成功')
  }).catch(() => {
    // 取消操作
  })
}

const getStatusType = (status) => {
  const statusMap = {
    '进行中': 'primary',
    '已完成': 'success',
    '已取消': 'info'
  }
  return statusMap[status] || 'info'
}
</script>

<style scoped>
.plans-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.plan-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.plan-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.plan-image {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px 8px 0 0;
}

.plan-icon {
  color: white;
  opacity: 0.8;
}

.plan-content {
  padding: 16px;
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
  flex: 1;
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
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>

