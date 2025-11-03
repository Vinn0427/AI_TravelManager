import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  // 定义环境变量，支持从系统环境变量MAP_API_KEY读取
  envPrefix: ['VITE_'],
  define: {
    // 将系统环境变量MAP_API_KEY传递给前端（在构建时注入）
    'import.meta.env.MAP_API_KEY': JSON.stringify(process.env.MAP_API_KEY || process.env.VITE_MAP_API_KEY || '')
  }
})

