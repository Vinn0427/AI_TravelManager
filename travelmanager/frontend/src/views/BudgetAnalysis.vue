<template>
  <div class="budget-container">
    <div class="budget-header">
      <h1 class="page-title">预算分析</h1>
      <p class="page-subtitle">基于历史行程预算的数据洞察与建议</p>
    </div>

    <el-row :gutter="20">
      <el-col :xs="24" :md="10">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>类别汇总</span>
            </div>
          </template>
          <el-table :data="tableData" v-loading="loading" stripe>
            <el-table-column prop="category" label="类别" />
            <el-table-column prop="amount" label="总金额(元)" />
          </el-table>
          <div class="totals">
            <div>计划数量：{{ plansCount }}</div>
            <div>总支出：¥{{ totalAmount }}</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <el-icon><Edit /></el-icon>
              <span>AI评价</span>
            </div>
          </template>
          <el-skeleton :loading="loading" animated>
            <template #template>
              <el-skeleton-item variant="text" style="width: 100%" />
              <el-skeleton-item variant="text" style="width: 90%" />
              <el-skeleton-item variant="text" style="width: 95%" />
            </template>
            <template #default>
              <div class="ai-comment">{{ aiComment }}</div>
            </template>
          </el-skeleton>
        </el-card>
      </el-col>
    </el-row>
  </div>
 </template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { budgetApi } from '@/api/budget'
import { Document, Edit } from '@element-plus/icons-vue'

const loading = ref(false)
const tableData = ref([])
const totalAmount = ref(0)
const plansCount = ref(0)
const aiComment = ref('')

const loadData = async () => {
  try {
    loading.value = true
    const res = await budgetApi.analyze()
    if (res.code === 200 && res.data) {
      const totals = res.data.totalsByCategory || {}
      tableData.value = Object.keys(totals).map(k => ({ category: k, amount: totals[k] }))
      totalAmount.value = res.data.totalAmount || 0
      plansCount.value = res.data.plansCount || 0
      aiComment.value = res.data.aiComment || '暂无评价'
    } else {
      ElMessage.error(res.message || '获取预算分析失败')
    }
  } catch (e) {
    ElMessage.error('获取预算分析失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.budget-container {
  max-width: 1400px;
  margin: 0 auto;
}
.budget-header { margin-bottom: 20px; }
.page-title { font-size: 28px; font-weight: 600; color: #303133; margin-bottom: 8px; }
.page-subtitle { font-size: 14px; color: #909399; }
.card-header { display: flex; align-items: center; gap: 8px; font-weight: 600; }
.totals { display: flex; justify-content: space-between; margin-top: 12px; color: #606266; }
.ai-comment { white-space: pre-wrap; line-height: 1.8; color: #303133; }
</style>


