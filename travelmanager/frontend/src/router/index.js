import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import Layout from '@/components/Layout.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Home from '@/views/Home.vue'
import Plan from '@/views/Plan.vue'
import Plans from '@/views/Plans.vue'
import Profile from '@/views/Profile.vue'
import Map from '@/views/Map.vue'
import { authUtils } from '@/utils/auth'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: Home,
        meta: { requiresAuth: true, title: '首页' }
      },
      {
        path: 'plan',
        name: 'Plan',
        component: Plan,
        meta: { requiresAuth: true, title: '旅行规划' }
      },
      {
        path: 'plans',
        name: 'Plans',
        component: Plans,
        meta: { requiresAuth: true, title: '我的行程' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile,
        meta: { requiresAuth: true, title: '个人中心' }
      },
      {
        path: 'map',
        name: 'Map',
        component: Map,
        meta: { requiresAuth: true, title: '地图导航' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const isLoggedIn = authUtils.isLoggedIn()
  
  // 检查路由是否需要认证
  if (to.meta.requiresAuth) {
    if (isLoggedIn) {
      // 已登录，允许访问
      next()
    } else {
      // 未登录，跳转到登录页
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  } else {
    // 不需要认证的路由
    if (to.path === '/login' || to.path === '/register') {
      if (isLoggedIn) {
        // 已登录，访问登录/注册页时跳转到首页
        next('/home')
      } else {
        next()
      }
    } else {
      next()
    }
  }
})

export default router

