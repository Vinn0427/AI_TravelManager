import request from '@/utils/request'

/**
 * AI相关API（大模型接口）
 */
export const aiApi = {
  /**
   * 生成旅行行程（调用阿里百炼大模型）
   * @param {object} data - 行程请求数据
   * @param {string} data.destination - 目的地
   * @param {string} data.startDate - 出发日期 (YYYY-MM-DD)
   * @param {number} data.days - 旅行天数
   * @param {number} data.budget - 预算（元）
   * @param {number} data.people - 同行人数
   * @param {string[]} data.preferences - 旅行偏好列表
   * @param {string} data.requirements - 特殊需求
   */
  generatePlan(data) {
    return request({
      url: '/ai/generate-plan',
      method: 'post',
      data
    })
  },

  /**
   * 获取景点地理坐标（通过大模型解析行程中的景点）
   * @param {object} data - 包含行程信息的数据
   */
  getAttractionsLocation(data) {
    return request({
      url: '/ai/get-attractions-location',
      method: 'post',
      data
    })
  }
}

