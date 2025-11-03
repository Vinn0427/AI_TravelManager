/**
 * 高德地图工具类
 * 提供地理位置服务、路径规划等功能
 */
export class AmapUtil {
  constructor(apiKey) {
    this.apiKey = apiKey
    this.map = null
    this.markers = []
    this.polyline = null
  }

  /**
   * 初始化地图
   * @param {string} containerId - 地图容器ID
   * @param {object} options - 地图配置选项
   */
  initMap(containerId, options = {}) {
    return new Promise((resolve, reject) => {
      if (!window.AMap) {
        reject(new Error('高德地图API未加载，请检查是否已引入地图脚本'))
        return
      }

      const defaultOptions = {
        zoom: 13, // 缩放级别
        center: [116.397428, 39.90923], // 默认中心点（北京天安门）
        mapStyle: 'amap://styles/normal', // 地图样式
        ...options
      }

      this.map = new window.AMap.Map(containerId, defaultOptions)
      
      // 地图加载完成后返回
      this.map.on('complete', () => {
        resolve(this.map)
      })
    })
  }

  /**
   * 添加标记点
   * @param {number} lng - 经度
   * @param {number} lat - 纬度
   * @param {string} title - 标记标题
   * @param {object} options - 标记选项
   */
  addMarker(lng, lat, title, options = {}) {
    if (!this.map) {
      throw new Error('地图未初始化')
    }

    const marker = new window.AMap.Marker({
      position: [lng, lat],
      title: title,
      ...options
    })

    marker.setMap(this.map)
    this.markers.push(marker)
    
    return marker
  }

  /**
   * 清除所有标记
   */
  clearMarkers() {
    this.markers.forEach(marker => {
      marker.setMap(null)
    })
    this.markers = []
  }

  /**
   * 地理编码（地址转坐标）
   * @param {string} address - 地址
   * @returns {Promise<object>} 坐标信息
   */
  async geocode(address) {
    return new Promise((resolve, reject) => {
      if (!window.AMap) {
        reject(new Error('高德地图API未加载'))
        return
      }

      const geocoder = new window.AMap.Geocoder()
      geocoder.getLocation(address, (status, result) => {
        if (status === 'complete' && result.geocodes.length > 0) {
          const location = result.geocodes[0].location
          resolve({
            lng: location.lng,
            lat: location.lat,
            formattedAddress: result.geocodes[0].formattedAddress
          })
        } else {
          reject(new Error(`地理编码失败: ${status}`))
        }
      })
    })
  }

  /**
   * 逆地理编码（坐标转地址）
   * @param {number} lng - 经度
   * @param {number} lat - 纬度
   * @returns {Promise<object>} 地址信息
   */
  async reverseGeocode(lng, lat) {
    return new Promise((resolve, reject) => {
      if (!window.AMap) {
        reject(new Error('高德地图API未加载'))
        return
      }

      const geocoder = new window.AMap.Geocoder()
      geocoder.getAddress([lng, lat], (status, result) => {
        if (status === 'complete' && result.regeocode) {
          resolve({
            formattedAddress: result.regeocode.formattedAddress,
            addressComponent: result.regeocode.addressComponent
          })
        } else {
          reject(new Error(`逆地理编码失败: ${status}`))
        }
      })
    })
  }

  /**
   * 路径规划
   * @param {Array} waypoints - 途经点数组 [{lng, lat, name}]
   * @param {string} strategy - 路径策略（walking/driving/transit）
   */
  async planRoute(waypoints, strategy = 'walking') {
    if (!this.map) {
      throw new Error('地图未初始化')
    }

    // 清除之前的路线
    if (this.polyline) {
      this.map.remove(this.polyline)
    }

    if (waypoints.length < 2) {
      throw new Error('至少需要2个点进行路径规划')
    }

    return new Promise((resolve, reject) => {
      const routeType = {
        walking: window.AMap.DrivingPolicy.LEAST_TIME,
        driving: window.AMap.DrivingPolicy.LEAST_TIME,
        transit: window.AMap.TransferPolicy.LEAST_TIME
      }

      const driving = new window.AMap.Driving({
        map: this.map,
        policy: routeType[strategy] || routeType.walking
      })

      const points = waypoints.map(point => new window.AMap.LngLat(point.lng, point.lat))

      driving.search(
        points[0],
        points[points.length - 1],
        {
          waypoints: points.slice(1, -1) // 中间途经点
        },
        (status, result) => {
          if (status === 'complete') {
            if (result.routes && result.routes.length > 0) {
              // 绘制路径
              const route = result.routes[0]
              const path = []
              
              route.steps.forEach(step => {
                path.push(...step.path)
              })

              this.polyline = new window.AMap.Polyline({
                path: path,
                isOutline: true,
                outlineColor: '#ffeeff',
                borderWeight: 3,
                strokeColor: '#3366FF',
                strokeOpacity: 1,
                strokeWeight: 6,
                lineJoin: 'round',
                lineCap: 'round',
                zIndex: 50
              })

              this.map.add(this.polyline)
              this.map.setFitView([this.polyline])

              resolve({
                distance: route.distance, // 总距离（米）
                duration: route.duration, // 总时长（秒）
                path: path
              })
            } else {
              reject(new Error('未找到路径'))
            }
          } else {
            reject(new Error(`路径规划失败: ${status}`))
          }
        }
      )
    })
  }

  /**
   * 搜索周边POI（兴趣点）
   * @param {number} lng - 经度
   * @param {number} lat - 纬度
   * @param {string} type - POI类型（餐饮、住宿、景点、交通等）
   * @param {number} radius - 搜索半径（米）
   */
  async searchNearby(lng, lat, type, radius = 1000) {
    return new Promise((resolve, reject) => {
      if (!window.AMap) {
        reject(new Error('高德地图API未加载'))
        return
      }

      const placeSearch = new window.AMap.PlaceSearch({
        type: type,
        pageSize: 10,
        pageIndex: 1
      })

      placeSearch.searchNearBy(
        '',
        [lng, lat],
        radius,
        (status, result) => {
          if (status === 'complete' && result.poiList) {
            const pois = result.poiList.pois.map(poi => ({
              id: poi.id,
              name: poi.name,
              address: poi.address,
              location: {
                lng: poi.location.lng,
                lat: poi.location.lat
              },
              distance: poi.distance,
              tel: poi.tel,
              type: poi.type
            }))
            resolve(pois)
          } else {
            reject(new Error(`搜索失败: ${status}`))
          }
        }
      )
    })
  }

  /**
   * 设置地图中心点和缩放级别
   */
  setCenterAndZoom(lng, lat, zoom = 13) {
    if (this.map) {
      this.map.setCenter([lng, lat])
      this.map.setZoom(zoom)
    }
  }

  /**
   * 适应所有标记点
   */
  fitView() {
    if (this.map && this.markers.length > 0) {
      this.map.setFitView(this.markers)
    }
  }
}

