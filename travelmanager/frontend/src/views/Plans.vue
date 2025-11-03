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
            <el-option label="计划中" value="计划中" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
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
    <div v-loading="loading" element-loading-text="加载中...">
      <template v-if="!loading">
        <el-empty v-if="filteredPlans.length === 0 && plans.length === 0" description="暂无行程，快去创建您的第一个旅行计划吧！">
          <el-button type="primary" @click="goToPlan">创建行程</el-button>
        </el-empty>

        <el-empty v-else-if="filteredPlans.length === 0 && plans.length > 0" description="没有找到符合条件的行程">
          <el-button @click="handleReset">重置筛选</el-button>
        </el-empty>

        <el-row v-else :gutter="20">
      <el-col
        v-for="plan in filteredPlans"
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
              <h3>{{ getPlanTitle(plan) }}</h3>
              <el-tag :type="getStatusType(plan.status)" size="small">
                {{ plan.status }}
              </el-tag>
            </div>
            <div class="plan-info">
              <p><el-icon><Location /></el-icon> {{ plan.destination }}</p>
              <p><el-icon><Calendar /></el-icon> {{ plan.startDateFormatted }} - {{ plan.endDateFormatted }}</p>
              <p><el-icon><Timer /></el-icon> {{ plan.totalDays }} 天</p>
              <p><el-icon><Money /></el-icon> 预算: ¥{{ plan.budgetFormatted }}</p>
            </div>
            <div class="plan-footer">
              <el-button text type="primary" @click="viewPlan(plan.id)">查看详情</el-button>
              <el-button text type="danger" @click="deletePlan(plan.id)">删除</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
        </el-row>
      </template>
    </div>
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
import { planApi } from '@/api/plan'

const router = useRouter()
const filterForm = reactive({
  status: '',
  destination: ''
})

const plans = ref([])
const loading = ref(false)

// 获取行程列表
const loadPlans = async () => {
  loading.value = true
  try {
    const res = await planApi.getPlans()
    if (res.code === 200 && res.data) {
      // 处理数据，添加计算字段
      plans.value = res.data.map(plan => {
        return {
          ...plan,
          // 格式化日期
          startDateFormatted: formatDate(plan.startDate),
          endDateFormatted: formatDate(plan.endDate),
          // 计算状态
          status: calculateStatus(plan.startDate, plan.endDate),
          // 格式化预算
          budgetFormatted: formatBudget(plan.budget)
        }
      })
      
      // 应用筛选
      applyFilter()
    } else {
      ElMessage.error(res.message || '获取行程列表失败')
      plans.value = []
    }
  } catch (error) {
    console.error('获取行程列表失败:', error)
    ElMessage.error('获取行程列表失败')
    plans.value = []
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化预算
const formatBudget = (budget) => {
  if (!budget) return '0.00'
  return parseFloat(budget).toFixed(2)
}

// 计算状态：根据日期判断行程状态
const calculateStatus = (startDate, endDate) => {
  if (!startDate || !endDate) return '计划中'
  
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  const start = new Date(startDate)
  start.setHours(0, 0, 0, 0)
  
  const end = new Date(endDate)
  end.setHours(0, 0, 0, 0)
  
  if (today < start) {
    return '计划中'
  } else if (today >= start && today <= end) {
    return '进行中'
  } else {
    return '已完成'
  }
}

// 应用筛选
const filteredPlans = ref([])

const applyFilter = () => {
  let result = [...plans.value]
  
  // 按状态筛选
  if (filterForm.status) {
    result = result.filter(plan => plan.status === filterForm.status)
  }
  
  // 按目的地筛选
  if (filterForm.destination) {
    const keyword = filterForm.destination.toLowerCase()
    result = result.filter(plan => 
      plan.destination && plan.destination.toLowerCase().includes(keyword)
    )
  }
  
  filteredPlans.value = result
}

// 初始化加载
onMounted(() => {
  loadPlans()
})

const goToPlan = () => {
  router.push('/plan')
}

const handleFilter = () => {
  applyFilter()
}

const handleReset = () => {
  filterForm.status = ''
  filterForm.destination = ''
  applyFilter()
}

const viewPlan = (id) => {
  router.push(`/plan/${id}`)
}

const deletePlan = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个行程吗？删除后不可恢复！', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用删除API
    const res = await planApi.deletePlan(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      // 重新加载列表
      loadPlans()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除行程失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const getStatusType = (status) => {
  const statusMap = {
    '计划中': 'info',
    '进行中': 'primary',
    '已完成': 'success'
  }
  return statusMap[status] || 'info'
}

// 生成标题（使用目的地和日期范围）
const getPlanTitle = (plan) => {
  return `${plan.destination} - ${plan.startDateFormatted}`
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

