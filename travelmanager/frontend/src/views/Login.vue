<template>
  <div class="gradient-bg">
    <div class="auth-container">
      <div class="auth-card">
        <div class="icon-wrapper">
          <div class="icon-circle">
            <el-icon :size="40"><Location /></el-icon>
          </div>
        </div>
        <h1 class="auth-title">欢迎回来</h1>
        <p class="auth-subtitle">登录您的账户，开始规划完美旅程</p>
        
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="auth-form"
          label-position="top"
        >
          <el-form-item label="邮箱/用户名" prop="username" class="form-item">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入邮箱或用户名"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item label="密码" prop="password" class="form-item">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item class="form-item">
            <div style="display: flex; justify-content: space-between; width: 100%;">
              <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
              <el-link type="primary" :underline="false" style="font-size: 14px;">
                忘记密码？
              </el-link>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="submit-button"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-link">
          还没有账户？
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Location } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import { authUtils } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入邮箱或用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      
      try {
        const response = await userApi.login(loginForm)
        if (response.code === 200 && response.data) {
          // 保存token和用户信息
          authUtils.setToken(response.data.token)
          authUtils.setUserInfo(response.data.user)
          
          ElMessage.success('登录成功！')
          
          // 跳转到指定页面或首页
          const redirect = route.query.redirect || '/home'
          router.push(redirect)
        } else {
          ElMessage.error(response.message || '登录失败')
        }
      } catch (error) {
        console.error('登录错误:', error)
        ElMessage.error(error.message || '登录失败，请检查网络连接')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请填写完整的登录信息')
      return false
    }
  })
}
</script>

<style scoped>
/* 组件特定样式已放在 common.css 中 */
</style>

