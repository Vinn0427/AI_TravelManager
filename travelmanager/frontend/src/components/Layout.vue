<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="layout-header">
      <div class="header-left">
        <el-icon :size="24" class="logo-icon"><Location /></el-icon>
        <span class="logo-text">AI 旅行规划师</span>
      </div>
      <div class="header-right">
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          class="header-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/home">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/plan">
            <el-icon><MapLocation /></el-icon>
            <span>旅行规划</span>
          </el-menu-item>
          <el-menu-item index="/plans">
            <el-icon><Document /></el-icon>
            <span>我的行程</span>
          </el-menu-item>
          <el-menu-item index="/map">
            <el-icon><MapLocation /></el-icon>
            <span>地图导航</span>
          </el-menu-item>
        </el-menu>
        <el-dropdown @command="handleCommand">
          <span class="user-dropdown">
            <el-avatar :size="32" :src="userAvatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <span class="username">{{ userInfo?.username || '用户' }}</span>
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-main class="layout-main">
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Location,
  House,
  MapLocation,
  Document,
  User,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'
import { authUtils } from '@/utils/auth'

const router = useRouter()
const route = useRoute()

const userInfo = ref(authUtils.getUserInfo())
const userAvatar = ref('')
const activeMenu = ref('/home')

// 计算当前激活的菜单
const currentMenu = computed(() => {
  return route.path
})

// 监听路由变化
watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
}, { immediate: true })

onMounted(() => {
  activeMenu.value = route.path
})

// 处理菜单选择
const handleMenuSelect = (key) => {
  router.push(key)
  activeMenu.value = key
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    handleLogout()
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    authUtils.clearAuth()
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {
    // 取消操作
  })
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-header {
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
}

.logo-icon {
  color: white;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: white;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-menu {
  background: transparent;
  border: none;
}

:deep(.header-menu .el-menu-item) {
  color: rgba(255, 255, 255, 0.9);
  border-bottom: 2px solid transparent;
}

:deep(.header-menu .el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

:deep(.header-menu .el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  border-bottom-color: white;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background 0.3s;
}

.user-dropdown:hover {
  background: rgba(255, 255, 255, 0.1);
}

.username {
  font-size: 14px;
  font-weight: 500;
}

.layout-main {
  flex: 1;
  padding: 20px;
  background: #f5f7fa;
  overflow-y: auto;
}
</style>

