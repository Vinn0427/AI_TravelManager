<template>
  <div class="gradient-bg">
    <div class="auth-container">
      <div class="auth-card">
        <div class="icon-wrapper">
          <div class="icon-circle">
            <el-icon :size="40"><UserFilled /></el-icon>
          </div>
        </div>
        <h1 class="auth-title">创建账户</h1>
        <p class="auth-subtitle">注册新账户，开启智能旅行规划之旅</p>
        
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="auth-form"
          label-position="top"
        >
          <el-form-item label="用户名" prop="username" class="form-item">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名（3-20个字符）"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item label="邮箱" prop="email" class="form-item">
            <el-input
              v-model="registerForm.email"
              placeholder="请输入邮箱地址"
              size="large"
              :prefix-icon="Message"
              clearable
            />
          </el-form-item>

          <el-form-item label="手机号" prop="phone" class="form-item">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号（可选）"
              size="large"
              :prefix-icon="Phone"
              clearable
            />
          </el-form-item>

          <el-form-item label="密码" prop="password" class="form-item">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码（至少6个字符）"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword" class="form-item">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
              @keyup.enter="handleRegister"
            />
          </el-form-item>

          <el-form-item class="form-item">
            <el-checkbox v-model="registerForm.agreement">
              我已阅读并同意
              <el-link type="primary" :underline="false" style="margin: 0 4px;">
                《用户协议》
              </el-link>
              和
              <el-link type="primary" :underline="false" style="margin: 0 4px;">
                《隐私政策》
              </el-link>
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="submit-button"
              :loading="loading"
              @click="handleRegister"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-link">
          已有账户？
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Phone, UserFilled } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

// 自定义验证规则：确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 自定义验证规则：用户协议
const validateAgreement = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请先同意用户协议和隐私政策'))
  } else {
    callback()
  }
}

// 自定义验证规则：手机号
const validatePhone = (rule, value, callback) => {
  if (value === '') {
    callback()
  } else {
    const phoneReg = /^1[3-9]\d{9}$/
    if (!phoneReg.test(value)) {
      callback(new Error('请输入正确的手机号码'))
    } else {
      callback()
    }
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '用户名只能包含字母、数字、下划线和中文', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{6,}$/, message: '密码至少包含大小写字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreement: [
    { validator: validateAgreement, trigger: 'change' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      
      try {
        const response = await userApi.register(registerForm)
        if (response.code === 200) {
          ElMessage.success('注册成功！正在跳转到登录页面...')
          
          // 注册成功后跳转到登录页
          setTimeout(() => {
            router.push('/login')
          }, 1500)
        } else {
          ElMessage.error(response.message || '注册失败')
        }
      } catch (error) {
        console.error('注册错误:', error)
        ElMessage.error(error.message || '注册失败，请检查网络连接')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('请填写完整的注册信息并同意用户协议')
      return false
    }
  })
}
</script>

<style scoped>
/* 组件特定样式已放在 common.css 中 */
</style>

