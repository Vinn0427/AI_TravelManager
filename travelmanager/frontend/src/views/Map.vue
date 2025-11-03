<template>
  <div class="map-container">
    <div class="map-header">
      <h1 class="page-title">地图导航</h1>
      <p class="page-subtitle">查看目的地位置和路线规划</p>
    </div>

    <el-row :gutter="20">
      <!-- 左侧：搜索和控制面板 -->
      <el-col :xs="24" :md="6">
        <el-card class="control-card">
          <template #header>
            <div class="card-header">
              <el-icon><Search /></el-icon>
              <span>搜索目的地</span>
            </div>
          </template>

          <el-form :model="searchForm" label-position="top">
            <el-form-item label="目的地">
              <el-input
                v-model="searchForm.destination"
                placeholder="输入城市或景点名称"
                clearable
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Location /></el-icon>
                </template>
                <template #suffix>
                  <el-button
                    :icon="Search"
                    circle
                    text
                    @click="handleSearch"
                    title="搜索"
                  />
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                style="width: 100%"
                @click="handleSearch"
              >
                搜索
              </el-button>
            </el-form-item>
          </el-form>

          <el-divider />

          <div class="map-controls">
            <h3>地图控制</h3>
            <el-button-group style="width: 100%">
              <el-button :icon="ZoomIn" @click="handleZoomIn">放大</el-button>
              <el-button :icon="ZoomOut" @click="handleZoomOut">缩小</el-button>
            </el-button-group>
            <el-button
              type="primary"
              style="width: 100%; margin-top: 10px"
              :icon="Aim"
              @click="handleLocate"
            >
              定位到当前位置
            </el-button>
          </div>

          <el-divider />

          <div class="location-info" v-if="currentLocation">
            <h3>当前位置</h3>
            <p><strong>经度:</strong> {{ currentLocation.lng }}</p>
            <p><strong>纬度:</strong> {{ currentLocation.lat }}</p>
            <p><strong>地址:</strong> {{ currentLocation.address || '正在获取...' }}</p>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：地图展示区域 -->
      <el-col :xs="24" :md="18">
        <el-card class="map-card">
          <template #header>
            <div class="card-header">
              <el-icon><MapLocation /></el-icon>
              <span>地图视图</span>
              <div style="margin-left: auto; display: flex; gap: 10px;">
                <el-button
                  :icon="FullScreen"
                  circle
                  text
                  @click="handleFullScreen"
                  title="全屏"
                />
              </div>
            </div>
          </template>

          <!-- 地图容器 -->
          <div id="amap-container" class="amap-container">
            <div class="map-placeholder">
              <el-icon :size="60" class="placeholder-icon"><MapLocation /></el-icon>
              <p class="placeholder-text">地图加载中...</p>
              <p class="placeholder-hint">
                提示：此页面已预留高德地图 API 集成接口
              </p>
              <p class="placeholder-hint">
                请配置高德地图 API Key 以显示地图
              </p>
            </div>
          </div>

          <!-- 地图操作提示 -->
          <div class="map-tips">
            <el-alert
              title="地图功能说明"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                <ul class="tips-list">
                  <li>支持搜索目的地并在地图上标记</li>
                  <li>支持路线规划功能</li>
                  <li>支持实时位置定位</li>
                  <li>支持地图缩放和拖拽操作</li>
                </ul>
              </template>
            </el-alert>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Location,
  MapLocation,
  Search,
  ZoomIn,
  ZoomOut,
  Aim,
  FullScreen
} from '@element-plus/icons-vue'

const searchForm = reactive({
  destination: ''
})

const currentLocation = ref(null)
const mapInstance = ref(null)
const mapZoom = ref(13)

// 搜索目的地
const handleSearch = () => {
  if (!searchForm.destination.trim()) {
    ElMessage.warning('请输入目的地')
    return
  }

  AMap.plugin('AMap.Geocoder', () => {
    const geocoder = new AMap.Geocoder()
    geocoder.getLocation(searchForm.destination, (status, result) => {
      if (status === 'complete' && result.geocodes.length) {
        const location = result.geocodes[0].location
        mapInstance.value.setCenter(location)
        mapInstance.value.setZoom(15)

        new AMap.Marker({
          map: mapInstance.value,
          position: location,
          title: searchForm.destination,
        })

        ElMessage.success(`已定位到：${searchForm.destination}`)
      } else {
        ElMessage.error('未找到相关地点')
      }
    })
  })
}


// 地图缩放
const handleZoomIn = () => {
  mapZoom.value = Math.min(mapZoom.value + 1, 18)
  mapInstance.value.setZoom(mapZoom.value)
}

const handleZoomOut = () => {
  mapZoom.value = Math.max(mapZoom.value - 1, 3)
  mapInstance.value.setZoom(mapZoom.value)
}


// 定位当前位置
const handleLocate = () => {
  ElMessage.info('正在定位中...')

  AMap.plugin('AMap.Geolocation', () => {
    const geolocation = new AMap.Geolocation({
      enableHighAccuracy: true,
      timeout: 10000,
      position: 'RB',
    })
    mapInstance.value.addControl(geolocation)
    geolocation.getCurrentPosition((status, result) => {
      if (status === 'complete' && result.position) {
        const { lng, lat } = result.position
        currentLocation.value = {
          lng,
          lat,
          address: result.formattedAddress || '未知位置',
        }
        mapInstance.value.setCenter([lng, lat])
        ElMessage.success('定位成功')
      } else {
        ElMessage.error('定位失败，请检查浏览器定位权限')
      }
    })
  })
}


// 全屏显示
const handleFullScreen = () => {
  const container = document.getElementById('amap-container')
  if (!document.fullscreenElement) {
    container.requestFullscreen().catch(err => {
      ElMessage.warning('无法进入全屏模式')
    })
  } else {
    document.exitFullscreen()
  }
}

// 初始化地图
const initMap = () => {
  if (!window.AMap) {
    ElMessage.error('高德地图加载失败，请检查API Key或网络')
    return
  }

  // 创建地图实例
  mapInstance.value = new AMap.Map('amap-container', {
    zoom: mapZoom.value,
    center: [116.397428, 39.90923], // 默认中心：天安门
  })

  // 添加缩放控件
  AMap.plugin(['AMap.ToolBar'], () => {
    const toolbar = new AMap.ToolBar()
    mapInstance.value.addControl(toolbar)
  })

  ElMessage.success('地图初始化成功')
}


onMounted(() => {
  // 延迟初始化，确保DOM已加载
  setTimeout(() => {
    initMap()
  }, 100)
})

onUnmounted(() => {
  // 清理地图实例
  if (mapInstance.value) {
    mapInstance.value.destroy()
  }
})
</script>

<style scoped>
.map-container {
  max-width: 1400px;
  margin: 0 auto;
}

.map-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
}

.control-card {
  height: 100%;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.map-controls h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.location-info {
  margin-top: 20px;
}

.location-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.location-info p {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.6;
}

.map-card {
  height: 100%;
}

.amap-container {
  width: 100%;
  height: 600px;
  background: #f5f7fa;
  border-radius: 4px;
  position: relative;
  overflow: hidden;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.placeholder-icon {
  margin-bottom: 20px;
  opacity: 0.8;
}

.placeholder-text {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.placeholder-hint {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 8px;
  text-align: center;
}

.map-tips {
  margin-top: 20px;
}

.tips-list {
  margin: 10px 0 0 0;
  padding-left: 20px;
  font-size: 14px;
  line-height: 1.8;
}

.tips-list li {
  margin-bottom: 4px;
}

@media (max-width: 768px) {
  .amap-container {
    height: 400px;
  }
}
</style>

