<template>
  <div :id="mapId" class="amap-container" :style="{ height: height, width: width }"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { AmapUtil } from '@/utils/amap'

const props = defineProps({
  apiKey: {
    type: String,
    required: true
  },
  height: {
    type: String,
    default: '500px'
  },
  width: {
    type: String,
    default: '100%'
  },
  center: {
    type: Array,
    default: () => [116.397428, 39.90923]
  },
  zoom: {
    type: Number,
    default: 13
  },
  waypoints: {
    type: Array,
    default: () => []
  },
  routeStrategy: {
    type: String,
    default: 'walking' // walking, driving, transit
  }
})

const emit = defineEmits(['map-ready', 'route-planned'])

const mapId = ref(`amap-${Date.now()}`)
const amapUtil = ref(null)

onMounted(async () => {
  try {
    amapUtil.value = new AmapUtil(props.apiKey)
    await amapUtil.value.initMap(mapId.value, {
      center: props.center,
      zoom: props.zoom
    })
    emit('map-ready', amapUtil.value)
  } catch (error) {
    console.error('地图初始化失败:', error)
  }
})

watch(() => props.waypoints, async (newWaypoints) => {
  if (amapUtil.value && newWaypoints.length >= 2) {
    try {
      const routeResult = await amapUtil.value.planRoute(newWaypoints, props.routeStrategy)
      emit('route-planned', routeResult)
    } catch (error) {
      console.error('路径规划失败:', error)
    }
  }
}, { deep: true })

onBeforeUnmount(() => {
  if (amapUtil.value) {
    amapUtil.value.clearMarkers()
  }
})

defineExpose({
  addMarker: (lng, lat, title) => amapUtil.value?.addMarker(lng, lat, title),
  clearMarkers: () => amapUtil.value?.clearMarkers(),
  searchNearby: (lng, lat, type, radius) => amapUtil.value?.searchNearby(lng, lat, type, radius),
  geocode: (address) => amapUtil.value?.geocode(address)
})
</script>

<style scoped>
.amap-container {
  border-radius: 8px;
  overflow: hidden;
}
</style>

