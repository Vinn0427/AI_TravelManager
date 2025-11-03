import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import router from './router'
import App from './App.vue'
import './style/common.css'

// 动态加载高德地图API
function loadAmapScript() {
  return new Promise((resolve, reject) => {
    // 如果已经加载过，直接返回
    if (window.AMap) {
      resolve()
      return
    }

    // 从环境变量获取API Key
    // 优先使用VITE_MAP_API_KEY，如果没有则使用MAP_API_KEY（通过vite.config.js传递）
    const apiKey = import.meta.env.VITE_MAP_API_KEY || import.meta.env.MAP_API_KEY || ''
    
    if (!apiKey) {
      console.warn('未配置地图API Key环境变量（MAP_API_KEY 或 VITE_MAP_API_KEY），地图功能可能无法使用')
      console.warn('请在系统环境变量中设置 MAP_API_KEY，或创建 .env 文件设置 VITE_MAP_API_KEY')
      // 仍然尝试加载，但使用空key（可能会失败）
    } else {
      console.log('已从环境变量读取地图API Key')
    }

    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${apiKey}&plugin=AMap.Geocoder,AMap.Geolocation,AMap.PlaceSearch`
    script.onload = () => {
      console.log('高德地图API加载成功')
      resolve()
    }
    script.onerror = () => {
      console.error('高德地图API加载失败，请检查网络或API Key配置')
      reject(new Error('高德地图API加载失败'))
    }
    document.head.appendChild(script)
  })
}

// 在应用启动前加载地图API
loadAmapScript()
  .then(() => {
    initApp()
  })
  .catch((error) => {
    console.error('地图API加载失败:', error)
    // 即使地图加载失败，仍然启动应用
    initApp()
  })

function initApp() {
  const app = createApp(App)

  // 注册所有图标
  for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }

  app.use(ElementPlus)
  app.use(router)
  app.mount('#app')
}

